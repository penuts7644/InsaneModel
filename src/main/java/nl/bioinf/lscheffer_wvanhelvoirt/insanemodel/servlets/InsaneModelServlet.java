/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.configuration.ConfigurationPaths;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.SimulationBuilder;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Wout van Helvoirt
 */
@WebServlet(name = "InsaneModelServlet", urlPatterns = {"/InsaneModelServlet"})
@MultipartConfig
public class InsaneModelServlet extends HttpServlet {

    private String streamToString(InputStream stream) throws IOException {
        StringBuilder builder;
        BufferedReader reader;
        String line;

        builder = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(stream));
        line = null;

        while (((line = reader.readLine()) != null)) {
            builder.append(line);
        }

        stream.close();

        return builder.toString();
    }

    private JSONObject parseFormInputPart(Part masterPart) throws IOException, ParseException {
        String jsonString;
        JSONParser parser;
        JSONObject outputJson;

        jsonString = this.streamToString(masterPart.getInputStream());
        parser = new JSONParser();
        outputJson = (JSONObject) parser.parse(jsonString);

        return outputJson;
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session;
        String fileGiven;
        String infilePath = "";
        Part filePart;
        Part masterPart;

        session = request.getSession();

        // test if there was an input file given
        fileGiven = this.streamToString(request.getPart("wasFileGiven").getInputStream());

        // if given, save the input file
        if ("true".equals(fileGiven)) {
            filePart = request.getPart("file");
            infilePath = ConfigurationPaths.getAbsoluteInFilePath(session.getId());
            Path pathToInputFile = Paths.get(infilePath);
            Files.copy(filePart.getInputStream(), pathToInputFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // parse the master json
        masterPart = request.getPart("master");

        try {
            JSONObject insaneSettings = this.parseFormInputPart(masterPart);

            SimulationBuilder simbuild = new SimulationBuilder(insaneSettings,
                    ConfigurationPaths.getPathToInsane(),
                    infilePath,
                    ConfigurationPaths.getAbsoluteOutFilePath(session.getId()));

            Process insaneProcess = simbuild.build();
            insaneProcess.waitFor();
            Thread.sleep(2500);

            JSONObject outputJson = new JSONObject();

            if (insaneProcess.exitValue() != 0) {
                List<String> errors = simbuild.getErrorMessages();
                errors.add("insane.py exited with a non-zero exit value, so no output file has been written. Please"
                        + " check your given arguments and/or input file and try again.");
                outputJson.put("errorMessages", JSONArray.toJSONString(errors));
                outputJson.put("outfile", "no_output_file_available");
            } else {
                outputJson.put("errorMessages", JSONArray.toJSONString(simbuild.getErrorMessages()));
                outputJson.put("outfile", ConfigurationPaths.getWebOutFilePath(session.getId()));
            }

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.write(outputJson.toString());
            out.flush();
            out.close();
        } catch (ParseException ex) {
            //Logger.getLogger(InsaneModelServlet.class.getName()).log(Level.SEVERE, null, ex);
            // iets aan de warnings toevoegen?
        } catch (InterruptedException ex) {
            Logger.getLogger(InsaneModelServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    } // </editor-fold>

}
