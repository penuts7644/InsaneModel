/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.io.IOException;
import java.util.LinkedList;
import org.json.simple.JSONObject;

/**
 *
 * @author lonneke
 */
public class MartinateSimulationBuilder extends SimulationBuilder {
    private MartinizeSimulationBuilder martinizeBuild;
    private InsaneSimulationBuilder insaneBuild;
    private final String infilePath;
    private final String outfilePath;
    private final String martinatePath;
    

    public MartinateSimulationBuilder(JSONObject settings, String infilePath, String outfilePath, String martinatePath) {
        super(settings, infilePath);
//        this.martinizeBuild = new MartinizeSimulationBuilder(settings);
        this.insaneBuild = new InsaneSimulationBuilder(settings);
        this.infilePath = infilePath;
        this.outfilePath = outfilePath;
        this.martinatePath = martinatePath;
        this.martinizeBuild = new MartinizeSimulationBuilder(settings);
        this.insaneBuild = new InsaneSimulationBuilder(settings);
        this.buildArguments();
        this.isBuildable = true;
        
    }
    
    
    

    
//        
//    /** The absolute(!) path to insane.py. */
//    private final String martinizePath;
//    private final String outTopologyPath;
//    private final String outPdbPath;
//    private final String outIndexPath;
//
//    public MartinizeSimulationBuilder(JSONObject settings,
//                                      String infilePath,
//                                      String outfilePath,
//                                      String martinizePath,
//                                      LinkedList errorMessages) {
//        super(settings, infilePath, errorMessages);
//        this.martinizePath = martinizePath;
//        this.outTopologyPath = this.replaceExtension(outfilePath, "-cg.top");
//        this.outPdbPath = this.replaceExtension(outfilePath, "-mart.pdb");
//        this.outIndexPath = this.replaceExtension(outfilePath, "-mart.ndx");
//        this.martinize = this.defineMartinize();
//    }
//    
//    


    private void buildArguments() {
        this.arguments.add(this.martinatePath);
        if (!"".equals(this.infilePath) && this.infilePath != null){
            this.arguments.add("-f");
            this.arguments.add(this.infilePath);
        }
        
        this.arguments.add("-gmxrc");   // GMXRC MOET ERGENS ANDERS (CONFIGPATHS?)& MOET OOK OPTIONEEL ZIJN
        this.arguments.add("/usr/local/gromacs/bin/GMXRC");
        this.arguments.add("-dir");
        this.arguments.add(this.outfilePath);
        this.martinizeBuild.addMartinateArguments(this.arguments);
        this.insaneBuild.addMartinateArguments(this.arguments);
        
    }
    
}
