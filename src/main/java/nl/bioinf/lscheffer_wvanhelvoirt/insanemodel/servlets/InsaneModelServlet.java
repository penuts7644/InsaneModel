/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.servlets;

import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.configuration.ConfigurationPaths;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.InsaneSimulationBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.MartinizeSimulationBuilder;

/**
 *
 * @author Wout van Helvoirt
 */
@WebServlet(name = "InsaneModelServlet", urlPatterns = {"/InsaneModelServlet"})
@MultipartConfig
public class InsaneModelServlet extends HttpServlet {
    
    private String infilePath;
    private File outputDir;
    private LinkedList errorMessages;
    private boolean display;
    private String sessionId;
 

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
        this.sessionId = request.getSession().getId();
        this.errorMessages = new LinkedList();
        boolean fileGiven = this.getInputFile(request);
        boolean runMartinize = false;

        // parse the master json
        Part masterPart = request.getPart("master");

        try {
            JSONObject settings = this.parseFormInputPart(masterPart);

            this.outputDir = new File(ConfigurationPaths.getAbsoluteOutFilePath(this.sessionId));
            if (!this.outputDir.exists()) {
                this.outputDir.mkdir();
            }
            
            try {
                runMartinize = Boolean.parseBoolean(settings.get("martinize").toString());
            } catch (IllegalArgumentException | NullPointerException ex) { } // runMartinize remains false
            
            if (fileGiven && runMartinize){
                this.runMartinize(settings);
            }

            int exitValInsane = this.runInsane(settings);
            this.returnOutput(response, this.errorMessages, exitValInsane);
            
        } catch (ParseException ex) {  // iets aan de warnings toevoegen?
        } catch (InterruptedException ex) {}
    }
    
    private boolean getInputFile(HttpServletRequest request) throws IOException, ServletException{
        // test if there was an input file given
        boolean fileGiven = Boolean.parseBoolean(this.streamToString(request.getPart("wasFileGiven").getInputStream()));

        // if given, save the input file
        if (fileGiven) {
            Part filePart = request.getPart("file");
            this.infilePath = ConfigurationPaths.getAbsoluteInFilePath(request.getSession().getId());
            Path pathToInputFile = Paths.get(this.infilePath);
            Files.copy(filePart.getInputStream(), pathToInputFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        return fileGiven;
    }
    
    private int runMartinize(JSONObject settings) throws IOException, InterruptedException{
        MartinizeSimulationBuilder martbuild = new MartinizeSimulationBuilder(settings, 
                        this.infilePath,
                        this.outputDir.getPath() + System.getProperty("file.separator") + "output_martinate",
                        ConfigurationPaths.getPathToMartinize(),
                        this.errorMessages);
        Process martinizeProcess = martbuild.build();
        martinizeProcess.waitFor();
        Thread.sleep(2500);
        this.infilePath = martbuild.getOutputPdbPath();
        return martinizeProcess.exitValue();
    }
    
    private int runInsane(JSONObject settings) throws IOException, InterruptedException{
        InsaneSimulationBuilder simbuild = new InsaneSimulationBuilder(settings,
                    this.infilePath,
                    this.outputDir.getPath() + System.getProperty("file.separator") + "output_insane.gro",
                    ConfigurationPaths.getPathToInsane(),
                    this.errorMessages);

        Process insaneProcess = simbuild.build();
        // Only display if the grid is not too big
        this.display = !simbuild.isTooBig();
        insaneProcess.waitFor();
        Thread.sleep(2500);
        return insaneProcess.exitValue();
    }
    
    private void returnOutput(HttpServletResponse response, List<String> errors, int insaneExitValue) throws IOException{
        JSONObject outputJson = new JSONObject();

        if (insaneExitValue != 0) {
            //List<String> errors = simbuild.getErrorMessages();
            errors.add("insane.py exited with a non-zero exit value, so no output file has been written. Please"
                + " check your given arguments and/or input file and try again.");
            outputJson.put("errorMessages", JSONArray.toJSONString(errors));
            outputJson.put("outfile", "no_output_available");
            outputJson.put("download", false);
            outputJson.put("display", false);
        } else {
            outputJson.put("errorMessages", JSONArray.toJSONString(errors));
            outputJson.put("outfile", ConfigurationPaths.getWebOutFilePath(this.sessionId
                    + System.getProperty("file.separator")+ "output_insane.gro"));
            outputJson.put("download", true);
            outputJson.put("display", this.display);
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(outputJson.toString());
        out.flush();
        out.close();
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
