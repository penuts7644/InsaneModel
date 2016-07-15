/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author lonneke
 */
public class MartinizeSimulationBuilder extends SimulationBuilder {
    
    /** The absolute(!) path to insane.py. */
    private final String martinizePath;

    public MartinizeSimulationBuilder(JSONObject settings, String infilePath, String outfilePath, String martinizePath) {
        super(settings, infilePath, outfilePath);
        this.martinizePath = martinizePath;
    }

    @Override
    public Process build() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
