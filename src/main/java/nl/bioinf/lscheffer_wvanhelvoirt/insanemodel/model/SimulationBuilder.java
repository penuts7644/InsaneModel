/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class parses the given insane settings (a JSON object), and calls insane.py.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public class SimulationBuilder {
    /** The error message list all classes add error messages to. */
    private final List<String> errorMessages;
    /** The argument list all classes add arguments to. */
    private final List<String> arguments;
    /** The JSONObject containing all insaneSettings. */
    private final JSONObject insaneSettings;
    /** The GridSize for this simulation. */
    private final GridSize gridSize;
    /** The Membrane for this simulation. */
    private final Membrane membrane;
    /** The Protein for this simulation. */
    private final Protein protein;
    /** The Solvent for this simulation. */
    private final Solvent solvent;
    /** The absolute(!) path to insane.py. */
    private final String insanePath;
    /** The absolute(!) path to the input file. */
    private final String infilePath;
    /** The absolute(!) path to the output file. */
    private final String outfilePath;

    /**
     * Create a new SimulationBuilder, given the insane settings, the path to insane and the output file path.
     *
     * @param insaneSettings JSONObject containing all insane settings
     * @param insanePath     the absolute(!) path to insane
     * @param infilePath     the absolute(!) path to the input file.
     * @param outfilePath    the absolute(!) path to the output file
     */
    public SimulationBuilder(final JSONObject insaneSettings,
                             final String insanePath,
                             final String infilePath,
                             final String outfilePath) {
        this.errorMessages = new LinkedList();
        this.arguments = new LinkedList();
        this.insaneSettings = insaneSettings; // insanesettings must be set before gridsize/membrane/protein/solvent!
        this.gridSize = this.defineGridSize(); // gridsize must be set before membrane/solvent!
        this.membrane = this.defineMembrane();
        this.infilePath = infilePath; // infilepath must be set before defineProtein!
        this.protein = this.defineProtein();
        this.solvent = this.defineSolvent();
        this.insanePath = insanePath;
        this.outfilePath = outfilePath;
        this.testIfSimple();
    }

    /**
     * Define the GridSize for this simulation.
     *
     * @return the created GridSize
     */
    private GridSize defineGridSize() {
        return new GridSize(this.errorMessages,
                            this.getParameterDouble("parameter_d"),
                            this.getParameterDouble("parameter_x"),
                            this.getParameterDouble("parameter_y"),
                            this.getParameterDouble("parameter_z"),
                            this.getParameterBool("parameter_dz"),
                            this.getParameterString("parameter_pbc"));
    }

    private Integer[] getValidRatios(String ratioString) {
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

    private int getRatioInt(String stringForm) {
        try {
            return Integer.parseInt(stringForm);
        } catch (NumberFormatException nf) {
            return 1;
        }
    }

    /**
     * Define the StandardLipids for this simulation.
     *
     * @return the created array of StandardLipids("parameter_d"
     */
    private StandardLipid[] defineStandardLipids() {
        ValidLipidType type;
        Integer[] upperLowerRatio;

        // [["supertype", "subtype", "ratio"], ["supertype", "subtype", "ratio"], ["supertype", "subtype", "ratio"]]
        JSONArray lipidArray = (JSONArray) this.insaneSettings.get("parameter_l");
        List<StandardLipid> listStandLip = new LinkedList();

        for (Object lipid : lipidArray) {
            // ["supertype", "subtype", "ratio"]
            JSONArray singleLipid = (JSONArray) (lipid);

            try {
                // the second element is the lipid type
                type = ValidLipidType.valueOf(singleLipid.get(1).toString());

                // the third element are the ratios (single number or #:#)
                upperLowerRatio = this.getValidRatios(singleLipid.get(2).toString());

                listStandLip.add(new StandardLipid(type, upperLowerRatio[0], upperLowerRatio[1]));

                // illegalArgumentException when trying to get the enum value of the string
            } catch (IllegalArgumentException illArg) {
                // This probably never happens, just in case there is a typo on the website,
                // or if someone alters the form. Add an error message and ignore the lipid.
                if (!"".equals(singleLipid.get(0).toString())) {
                    errorMessages.add("Lipid type '" + singleLipid.get(0).toString()
                            + "' could not be recognized and has been ignored.");
                }
            } catch (NullPointerException np) {
                // might happen if a lipid is removed from the json, after creating it, ignore it
            }
        }

        return listStandLip.toArray(new StandardLipid[listStandLip.size()]);
    }

    /**
     * Define the AdditionalLipids for this simulation.
     *
     * @return the created array of AdditionalLipids
     */
    private AdditionalLipid[] defineAdditionalLipids() {
        String[] heads;
        String[] linkers;
        String[] tails;
        Integer[] upperLowerRatio;

        AdditionalLipid.resetCounter();

        // [["head", "linker", "tail", "ratio"], ["head", "linker", "tail", "ratio"]]
        JSONArray lipidArray = (JSONArray) this.insaneSettings.get("parameter_al");
        List<AdditionalLipid> listAddLip = new LinkedList();

        for (Object lipid : lipidArray) {
            // ["head", "linker", "tail", "ratio"]
            JSONArray singleLipid = (JSONArray) (lipid);

            // If there are any linkers given, try to create a lipid out of it
            if (!("".equals(singleLipid.get(1).toString()))) {
                heads = singleLipid.get(0).toString().toUpperCase().split(" ");
                linkers = singleLipid.get(1).toString().toUpperCase().split(" ");
                tails = singleLipid.get(2).toString().toUpperCase().split(" ");
                upperLowerRatio = this.getValidRatios(singleLipid.get(3).toString());

                listAddLip.add(new AdditionalLipid(this.errorMessages, heads, linkers, tails, upperLowerRatio[0],
                        upperLowerRatio[1]));
            }
        }

        return listAddLip.toArray(new AdditionalLipid[listAddLip.size()]);
    }

    /**
     * Define the Membrane for this simulation.
     *
     * @return the created Membrane
     */
    private Membrane defineMembrane() {
        return new Membrane(this.errorMessages,
                            this.defineStandardLipids(),
                            this.defineAdditionalLipids(),
                            this.gridSize,
                            this.getParameterDouble("parameter_rand"),
                            this.getParameterDouble("parameter_a"),
                            this.getParameterDouble("parameter_au"),
                            this.getParameterInt("parameter_asym"),
                            this.getParameterDouble("parameter_hole"),
                            this.getParameterDouble("parameter_disc"),
                            this.getParameterDouble("parameter_bd"));
    }

    /**
     * Define the Protein for this simulation.
     *
     * @return the created Protein
     */
    private Protein defineProtein() {
        return new Protein(this.errorMessages,
                            this.infilePath,
                            this.gridSize,
                            this.getParameterBool("parameter_ring"),
                            this.getParameterBool("parameter_center"),
                            this.getParameterString("parameter_rotate"),
                            this.getParameterDouble("parameter_fudge", -1),
                            this.getParameterDouble("parameter_dm"));
    }


    /**
     * Define the Solvent for this simulation.
     *
     * @return the created Solvent
     */
    private Solvent defineSolvent() {
        // solventArray contains [[supertype, subtype, ratio], [supertype, subtype, ratio]]
        JSONArray solventArray = (JSONArray) insaneSettings.get("parameter_sol");

        List<ValidSolventType> solventTypes = new LinkedList();
        List<Integer> solventRatios = new LinkedList();

        for (Object solventObject : solventArray) {
            // singleSolvent contains [supertype, subtype, ratio]
            JSONArray singleSolvent = (JSONArray) (solventObject);
            try {
                // try to add the types and ratios to the solventsubTypes (1) and solventRatios (2) arrays
                solventTypes.add(ValidSolventType.valueOf(singleSolvent.get(1).toString()));
                solventRatios.add(getRatioInt(singleSolvent.get(2).toString()));
            } catch (IllegalArgumentException illArg) {
                // if the solvent subtype (1) is a not-empty string, show an error message and ignore the solvent
                if (!"".equals(singleSolvent.get(1).toString())) {
                    errorMessages.add("Solvent type '" + singleSolvent.get(0).toString()
                            + "' could not be recognized and has been ignored.");
                }
            } catch (NullPointerException np) {
                // If the solvent is null, ignore it
            }
        }

        ValidSolventType[] solArray = solventTypes.toArray(new ValidSolventType[solventTypes.size()]);
        Integer[] intArray = solventRatios.toArray(new Integer[solventRatios.size()]);

        return new Solvent(this.errorMessages,
                            solArray,
                            intArray,
                            this.getParameterDouble("parameter_solr"),
                            this.getParameterDouble("parameter_sold"),
                            this.getParameterDouble("parameter_salt"),
                            this.getParameterInt("parameter_charge"));
    }

    private void testIfSimple() {
        if (!this.protein.isFileGiven() && this.solvent.onlySimpleSolvents() && this.membrane.onlySimpleLipids()) {
            this.errorMessages.add("There are no 'complex' molecules given (proteins, lipids containing a headgroup or"
                    + " complex solvent molecules). JSmol often doesn't display such simulations. To prevent this:"
                    + " please add at least one complex molecule.");
        }
    }

    /**
     * Try to convert the user input to a double, if it fails, return the given default value.
     *
     * @param parameterName the name of this parameter
     * @param defaultValue  default value if the conversion fails
     * @return              a valid double
     */
    private double getParameterDouble(final String parameterName, final double defaultValue) {
        try {
            return Double.parseDouble(this.insaneSettings.get(parameterName).toString());
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
    private double getParameterDouble(final String parameterName) {
        return getParameterDouble(parameterName, 0);
    }

    /**
     * Try to convert the user input to an integer, if it fails, return the given default value.
     *
     * @param parameterName the name of this parameter
     * @param defaultValue  default value if the conversion fails
     * @return              a valid integer
     */
    private int getParameterInt(final String parameterName, final double defaultValue) {
        return (int) Math.round(getParameterDouble(parameterName, defaultValue));
    }

    /**
     * Try to convert the user input to an integer, if it fails, return 0.
     *
     * @param parameterName the name of this parameter
     * @return              a valid integer
     */
    private int getParameterInt(final String parameterName) {
        return getParameterInt(parameterName, 0);
    }

    /**
     * Try to convert the user input to a boolean value, if it fails, return false.
     *
     * @param parameterName the name of this parameter
     * @return              a boolean value
     */
    private boolean getParameterBool(final String parameterName) {
        try {
            return Boolean.parseBoolean(this.insaneSettings.get(parameterName).toString());
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
    private String getParameterString(final String parameterName) {
        try {
            return this.insaneSettings.get(parameterName).toString();
        } catch (IllegalArgumentException | NullPointerException ex) {
            return "";
        }
    }

    /**
     * Build the simulation, start the process and return the process.
     *
     * @return the process
     * @throws IOException if an I/O error occurs
     */
    public Process build() throws IOException {
        this.arguments.add(this.insanePath);
        this.arguments.add("-o");
        this.arguments.add(this.outfilePath);
        this.gridSize.addArguments(this.arguments);
        this.membrane.addArguments(this.arguments);
        this.protein.addArguments(this.arguments);
        this.solvent.addArguments(this.arguments);

        ProcessBuilder processBuilder = new ProcessBuilder(this.arguments);
        return processBuilder.start();
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
