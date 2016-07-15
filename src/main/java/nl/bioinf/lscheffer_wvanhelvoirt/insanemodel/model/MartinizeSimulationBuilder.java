/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author lonneke
 */
public class MartinizeSimulationBuilder extends SimulationBuilder {
    
    private Martinize martinize;
    /** The absolute(!) path to insane.py. */
    private final String martinizePath;
    private final String outTopologyPath;
    private final String outPdbPath;
    private final String outIndexPath;

    public MartinizeSimulationBuilder(JSONObject settings, String infilePath, String martinizePath) {
        super(settings, infilePath);
        this.martinizePath = martinizePath;
        this.outTopologyPath = this.replaceExtension(infilePath, "-cg.top");
        this.outPdbPath = this.replaceExtension(infilePath, "-mart.pdb");
        this.outIndexPath = this.replaceExtension(infilePath, "-mart.ndx");
        this.martinize = this.defineMartinize();
    }
    

    private Martinize defineMartinize(){
        return new Martinize(this.errorMessages,
                            this.getParameterString("martinize_ss"),
                            this.getParameterBool("martinize_collagen"),
                            this.getParameterBool("martinize_nt"),
                            this.getParameterBool("martinize_cb"),
                            this.getParameterBool("martinize_cys"),
                            this.getParameterBool("martinize_link"),
                            this.getParameterBool("martinize_merge"),
                            this.getParameterString("martinize_p"),
                            this.getParameterInt("martinize_pf", 1000),
                            this.getParameterBool("martinize_ed"),
                            this.getParameterBool("martinize_sep"),
                            this.getParameterString("martinize_ff"));
    }
    
    private String replaceExtension(String filename, String newExtension){
        return FilenameUtils.removeExtension(filename) + newExtension;
    }

    @Override
    public Process build() throws IOException {
        this.arguments.add(this.martinizePath);
        this.arguments.add("-f");
        this.arguments.add(this.infilePath);
        this.arguments.add("-o");
        this.arguments.add(this.outTopologyPath);
        this.arguments.add("-x");
        this.arguments.add(this.outPdbPath);
        this.arguments.add("-n");
        this.arguments.add(this.outIndexPath);
        
        this.martinize.addArguments(arguments);
        
        for (String argument : this.arguments){
            System.out.println(argument);
        }


        ProcessBuilder processBuilder = new ProcessBuilder(this.arguments);
        return processBuilder.start();
    }
    
    
    public static void main(String [] args) throws IOException{
        MartinizeSimulationBuilder msb = new MartinizeSimulationBuilder(new JSONObject(), "infile.pdb", "martinize.py");
        msb.build();
    }

}
