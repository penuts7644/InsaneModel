/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author lonneke
 */
public abstract class SimulationBuilder {
    /** The error message list all classes add error messages to. */
    protected final List<String> errorMessages;
    /** The argument list all classes add arguments to. */
    protected final List<String> arguments;
    /** The JSONObject containing all settings. */
    protected final JSONObject settings;
    /** The absolute(!) path to the input file. */
    protected final String infilePath;
    

    public SimulationBuilder(JSONObject settings, String infilePath) {
        this(settings, infilePath, new LinkedList());
//        this.settings = settings;
//        this.errorMessages = new LinkedList();
//        this.arguments = new LinkedList();
//        this.infilePath = infilePath; 
        
    }
    
     public SimulationBuilder(JSONObject settings, String infilePath, LinkedList errorMessages) {
        this.settings = settings;
        this.errorMessages = errorMessages;
        this.arguments = new LinkedList();
        this.infilePath = infilePath; 
        
    }
    
    /**
     * Build the simulation, start the process and return the process.
     *
     * @return the process
     * @throws IOException if an I/O error occurs
     */
    public abstract Process build() throws IOException;
    
    
    protected int getRatioInt(String stringForm) {
        try {
            return Integer.parseInt(stringForm);
        } catch (NumberFormatException nf) {
            return 1;
        }
    }
    
    protected Integer[] getValidRatios(String ratioString) {
        Integer[] ratios = new Integer[2];
        String[] ratioStringArray = ratioString.split(":");

        switch (ratioStringArray.length) {
            case 1:
                // there is no 'ratio' given, just one single number. use that number
                ratios[0] = this.getRatioInt(ratioStringArray[0]);
                ratios[1] = this.getRatioInt(ratioStringArray[0]);
                break;
            case 2:
                // there are 2 values given (#:#)
                ratios[0] = this.getRatioInt(ratioStringArray[0]);
                ratios[1] = this.getRatioInt(ratioStringArray[1]);
                break;
            default:
                // if someone tries to give an invalid ratio (for instance 3:4:5)
                this.errorMessages.add("Relative abundance " + ratioString + " is not valid and has been set to 1.");
                ratios[0] = 1;
                ratios[1] = 1;
                break;
        }

        return ratios;
    }
    
        /**
     * Try to convert the user input to a double, if it fails, return the given default value.
     *
     * @param parameterName the name of this parameter
     * @param defaultValue  default value if the conversion fails
     * @return              a valid double
     */
    protected double getParameterDouble(final String parameterName, final double defaultValue) {
        try {
            return Double.parseDouble(this.settings.get(parameterName).toString());
        } catch (java.lang.NumberFormatException | NullPointerException ex) {
            return defaultValue;
        }
    }

    /**
     * Try to convert the user input to a double, if it fails, return 0.
     *
     * @param parameterName the name of this parameter
     * @return              a valid double
     */
    protected double getParameterDouble(final String parameterName) {
        return getParameterDouble(parameterName, 0);
    }

    /**
     * Try to convert the user input to an integer, if it fails, return the given default value.
     *
     * @param parameterName the name of this parameter
     * @param defaultValue  default value if the conversion fails
     * @return              a valid integer
     */
    protected int getParameterInt(final String parameterName, final double defaultValue) {
        return (int) Math.round(getParameterDouble(parameterName, defaultValue));
    }

    /**
     * Try to convert the user input to an integer, if it fails, return 0.
     *
     * @param parameterName the name of this parameter
     * @return              a valid integer
     */
    protected int getParameterInt(final String parameterName) {
        return getParameterInt(parameterName, 0);
    }

    /**
     * Try to convert the user input to a boolean value, if it fails, return false.
     *
     * @param parameterName the name of this parameter
     * @return              a boolean value
     */
    protected boolean getParameterBool(final String parameterName) {
        try {
            return Boolean.parseBoolean(this.settings.get(parameterName).toString());
        } catch (NullPointerException np) {
            return false;
        }
    }

    /**
     * Try to convert the user input to a String, if it fails, return an empty String.
     *
     * @param parameterName the name of this parameter
     * @return              a String
     */
    protected String getParameterString(final String parameterName) {
        try {
            return this.settings.get(parameterName).toString();
        } catch (IllegalArgumentException | NullPointerException ex) {
            return "";
        }
    }
    
    /**
     * Get the error messages list containing the error messages of the whole simulation.
     *
     * @return List containing all error messages
     */
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    /**
     * Get the argument list containing the command line arguments of the whole simulation.
     *
     * @return List containing all command line arguments
     */
    public List<String> getArguments() {
        return this.arguments;
    }
}
