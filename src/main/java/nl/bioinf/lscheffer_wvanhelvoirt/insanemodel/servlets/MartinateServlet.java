/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.servlets;

import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.configuration.ConfigurationPaths;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.InsaneSimulationBuilder;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.MartinizeSimulationBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model.MartinateSimulationBuilder;

/**
 *
 * @author Wout van Helvoirt
 */
@WebServlet(name = "MartinateServlet", urlPatterns = {"/MartinateServlet"})
@MultipartConfig
public class MartinateServlet extends HttpServlet {
    
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
        boolean fileGiven = this.getInputFile(request);
        boolean runMartinize = false;

        // parse the master json
        Part masterPart = request.getPart("master");
        

        try {
            
            JSONObject settings = this.parseFormInputPart(masterPart);
            

            this.outputDir = new File(ConfigurationPaths.getAbsoluteOutFilePath(this.sessionId));

            if (!this.outputDir.exists()) {
                this.outputDir.mkdir();
            } // OUTPUTDIR OP EEN MANIER LEEG MAKEN OM ALLES TE OVERSCHRIJVEN/OF NOG MAKKELIJKER; IN DEZE MAP EEN NIEUWE OUTPUTMAP

            int exitValInsane = this.runMartinate(settings);
            
            System.out.println("ER IS NOG GEEN OUTPUT");
            
            
//            try {
//                runMartinize = Boolean.parseBoolean(settings.get("martinize").toString());
//            } catch (IllegalArgumentException | NullPointerException ex) { } // runMartinize remains false
//            
//            if (fileGiven && runMartinize){
//                this.runMartinize(settings);
//            }
//
//            int exitValInsane = this.runInsane(settings);
//            this.zipOutputFiles();
//            this.returnOutput(response, this.errorMessages, exitValInsane);
//            
        } catch (ParseException | InterruptedException ex) {  // iets aan de warnings toevoegen?
        }
    }
    
    private int runMartinate(JSONObject settings) throws IOException, InterruptedException {

        MartinateSimulationBuilder martinateBuild = new MartinateSimulationBuilder(settings,
                                this.infilePath,
                                this.outputDir.getPath(),
                                ConfigurationPaths.getPathToMartinate());

        Process martinateProcess = martinateBuild.build();
        martinateProcess.waitFor();
        Thread.sleep(2500);
        System.out.println(martinateProcess.exitValue() + "=exitval");
        
        return martinateProcess.exitValue();
    }
//        MartinizeSimulationBuilder martbuild = new MartinizeSimulationBuilder(settings, 
//                        this.infilePath,
//                        this.outputDir.getPath() + System.getProperty("file.separator") + "output_martinate",
//                        ConfigurationPaths.getPathToMartinize(),
//                        this.errorMessages);
//        Process martinizeProcess = martbuild.build();
//        martinizeProcess.waitFor();
//        Thread.sleep(2500);
//        this.infilePath = martbuild.getOutputPdbPath();
//        return martinizeProcess.exitValue();
    
    
    
    private boolean getInputFile(HttpServletRequest request) throws IOException, ServletException {
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
    
//    private int runMartinize(JSONObject settings) throws IOException, InterruptedException {
//        MartinizeSimulationBuilder martbuild = new MartinizeSimulationBuilder(settings, 
//                        this.infilePath,
//                        this.outputDir.getPath() + System.getProperty("file.separator") + "output_martinate",
//                        ConfigurationPaths.getPathToMartinize(),
//                        this.errorMessages);
//        Process martinizeProcess = martbuild.build();
//        martinizeProcess.waitFor();
//        Thread.sleep(2500);
//        this.infilePath = martbuild.getOutputPdbPath();
//        return martinizeProcess.exitValue();
//    }
//    
//    private int runInsane(JSONObject settings) throws IOException, InterruptedException {
//        InsaneSimulationBuilder simbuild = new InsaneSimulationBuilder(settings,
//                    this.infilePath,
//                    this.outputDir.getPath() + System.getProperty("file.separator") + "output_insane.gro",
//                    ConfigurationPaths.getPathToInsane(),
//                    this.errorMessages);
//
//        Process insaneProcess = simbuild.build();
//        // Only display if the grid is not too big
//        this.display = !simbuild.isTooBig();
//        insaneProcess.waitFor();
//        Thread.sleep(2500);
//        return insaneProcess.exitValue();
//    }

    private void zipOutputFiles() throws IOException {
        // Make file from directory and search it for files.
        File f = new File(ConfigurationPaths.getAbsoluteOutFilePath(this.sessionId + System.getProperty("file.separator")));
        File[] listOfFiles = f.listFiles();

        // Create file and zip output streams.
        FileOutputStream fout = new FileOutputStream(new File(f.getAbsolutePath() + System.getProperty("file.separator") + "insane_model.zip"));
        ZipOutputStream zout = new ZipOutputStream(fout);

        // For each found file, make a new zip entry and write the file contents to it.
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                FileInputStream in = new FileInputStream(listOfFiles[i]);
                ZipEntry e = new ZipEntry("insane_model" + System.getProperty("file.separator") + listOfFiles[i].getName());
                zout.putNextEntry(e);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = in.read(bytes)) >= 0) {
                    zout.write(bytes, 0, length);
                }

                // Close entry.
                zout.closeEntry();
            }
        }

        // Close zip output stream.
        zout.close();
    }
    
//    private void returnOutput(HttpServletResponse response, List<String> errors, int insaneExitValue) throws IOException {
//        JSONObject outputJson = new JSONObject();
//
//        if (insaneExitValue != 0) {
//            //List<String> errors = simbuild.getErrorMessages();
//            errors.add("insane.py exited with a non-zero exit value, so no output file has been written. Please"
//                + " check your given arguments and/or input file and try again.");
//            outputJson.put("errorMessages", JSONArray.toJSONString(errors));
//            outputJson.put("outfile", "no_output_available");
//            outputJson.put("outfileZip", "no_output_available");
//            outputJson.put("download", false);
//            outputJson.put("display", false);
//        } else {
//            outputJson.put("errorMessages", JSONArray.toJSONString(errors));
//            outputJson.put("outfile", ConfigurationPaths.getWebOutFilePath(this.sessionId
//                    + System.getProperty("file.separator") + "output_insane.gro"));
//            outputJson.put("outfileZip", ConfigurationPaths.getWebOutFilePath(this.sessionId
//                    + System.getProperty("file.separator") + "insane_model.zip"));
//            outputJson.put("download", true);
//            outputJson.put("display", this.display);
//        }
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
//        out.write(outputJson.toString());
//        out.flush();
//        out.close();
//    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
