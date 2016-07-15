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
public class InsaneSimulationBuilder extends SimulationBuilder {
    
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
    /** The absolute(!) path to the output file. */
    protected final String outfilePath;
    

    /**
     * Create a new SimulationBuilder, given the insane settings, the path to insane and the output file path.
     *
     * @param settings JSONObject containing all insane settings
     * @param insanePath     the absolute(!) path to insane
     * @param infilePath     the absolute(!) path to the input file.
     * @param outfilePath    the absolute(!) path to the output file
     */
    public InsaneSimulationBuilder(final JSONObject settings,
                             final String infilePath,
                             final String outfilePath,
                             final String insanePath) {
        super(settings, infilePath); // settings must be set before gridsize/membrane/protein/solvent!
        this.outfilePath = outfilePath;
        this.gridSize = this.defineGridSize(); // gridsize must be set before membrane/solvent!
        this.membrane = this.defineMembrane();
        this.protein = this.defineProtein();
        this.solvent = this.defineSolvent();
        this.insanePath = insanePath;
        this.testIfSimple();
    }

    /**
     * Define the GridSize for this simulation.
     *
     * @return the created GridSize
     */
    private GridSize defineGridSize() {
        return new GridSize(this.errorMessages,
                            this.getParameterDouble("insane_d"),
                            this.getParameterDouble("insane_x"),
                            this.getParameterDouble("insane_y"),
                            this.getParameterDouble("insane_z"),
                            this.getParameterBool("insane_dz"),
                            this.getParameterString("insane_pbc"));
    }

    /**
     * Define the StandardLipids for this simulation.
     *
     * @return the created array of StandardLipids("insane_d"
     */
    private StandardLipid[] defineStandardLipids() {
        ValidLipidType type;
        Integer[] upperLowerRatio;

        // [["supertype", "subtype", "ratio"], ["supertype", "subtype", "ratio"], ["supertype", "subtype", "ratio"]]
        JSONArray lipidArray = (JSONArray) this.settings.get("insane_l");
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
        JSONArray lipidArray = (JSONArray) this.settings.get("insane_al");
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
                            this.getParameterDouble("insane_rand"),
                            this.getParameterDouble("insane_a"),
                            this.getParameterDouble("insane_au"),
                            this.getParameterInt("insane_asym"),
                            this.getParameterDouble("insane_hole"),
                            this.getParameterDouble("insane_disc"),
                            this.getParameterDouble("insane_bd"));
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
                            this.getParameterBool("insane_ring"),
                            this.getParameterBool("insane_center"),
                            this.getParameterString("insane_rotate"),
                            this.getParameterDouble("insane_fudge", -1),
                            this.getParameterDouble("insane_dm"));
    }


    /**
     * Define the Solvent for this simulation.
     *
     * @return the created Solvent
     */
    private Solvent defineSolvent() {
        // solventArray contains [[supertype, subtype, ratio], [supertype, subtype, ratio]]
        JSONArray solventArray = (JSONArray) settings.get("insane_sol");

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
                            this.getParameterDouble("insane_solr"),
                            this.getParameterDouble("insane_sold"),
                            this.getParameterDouble("insane_salt"),
                            this.getParameterInt("insane_charge"));
    }

    private void testIfSimple() {
        if (!this.protein.isFileGiven() && this.solvent.onlySimpleSolvents() && this.membrane.onlySimpleLipids()) {
            this.errorMessages.add("JSmol has trouble showing simulations containing only 'simple' solvents, "
                    + "user created lipids and some predefined lipids. "
                    + "To prevent this: add different molecules or increase the membrane/solvent random kick.");
        }
    }



    /**
     * Build the simulation, start the process and return the process.
     *
     * @return the process
     * @throws IOException if an I/O error occurs
     */
    @Override
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
     * Checks if the given grid size is 'too big' for JSmol to view.
     *
     * @return boolean whether grid is larger than MAX_GRID_SIZE_WITH_VIEW
     */
    public boolean isTooBig() {
        return this.gridSize.isTooBig();
    }
}
