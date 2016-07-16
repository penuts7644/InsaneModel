/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 * This class represents all selected solvents that will be used in insane.py.
 * This class contains an array of all used valid solvent types, and other solvent related parameters.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public class Solvent extends SettingManager {
    /** The solvent types to use. */
    private final ValidSolventType[] types;
    /** The ratios of the solvent types above. */
    private final Integer[] ratios;
    /** The random kick for the solvent. */
    private final double randomKick;
    /** The solvent particle diameter. */
    private final double diameter;
    /** The salt concentration. */
    private final double concentration;
    /** The overall charge. */
    private final int charge;


    /**
     * Create a new Solvent, containing all the different solvent types and ratios, and other parameters.
     *
     * @param errorMessages the list to add error messages to.
     * @param types         the solvent types to use
     * @param ratios        the ratios for the solvent types
     * @param randomKick    the solvent random kick
     * @param diameter      the solvent diameter
     * @param concentration the salt concentration
     * @param charge        the overall charge
     */
    public Solvent(final List<String> errorMessages,
                   final ValidSolventType[] types,
                   final Integer[] ratios,
                   final double randomKick,
                   final double diameter,
                   final double concentration,
                   final int charge) {
        super(errorMessages);
        this.types = types;
        this.ratios = ratios;
        this.randomKick = randomKick;
        this.diameter = this.validateDiameter(diameter);
        this.concentration = this.validateSaltConcentration(concentration);
        this.charge = charge;
    }

    /**
     * Validate the salt concentration; add an error message if the concentration is too high.
     *
     * @param concentration the given salt concentration
     * @return              the validated salt concentration
     */
    private double validateSaltConcentration(final double concentration) {
        // If the salt concentration is higher than 9.23333..., there is no room for any solvent.
        if (Math.abs(concentration) > 9.2333) {
            this.errorMessages.add("Your salt concentration is so high that there is no room for solvent available.");
        }
        return concentration;
    }

    /**
     * Validate the given solvent diameter.
     * The diameter can not be smaller than 0.35 (smaller diameters cause insane to be very slow),
     * and if the diameter is bigger than 3 a warning is added to errorMessages, but the value is not changed.
     *
     * @param givenDiameter the given solvent diameter
     * @return              the validated solvent diameter
     */
    private double validateDiameter(final double givenDiameter) {
        if (givenDiameter <= 0) {
            // Default value 0 or -1 is given;
            // return 0 so it will be ignored when building the command line representation,
            // and insane's default value will be used
            return 0;
        } else if (givenDiameter < 0.35) {
            // too small, change to 0.35
            this.errorMessages.add("Your solvent diameter '"
                    + givenDiameter + "' is too small and has been set to 0.35");
            return 0.35;
        } else if (givenDiameter > 3) {
            // very big, add a warning but do not change the diameter
            this.errorMessages.add("Your solvent diameter '"
                    + givenDiameter + "' is very big; this might cause your solvent not to be visible");
        }
        return givenDiameter;
    }

    /**
     * Add command line arguments of the Solvent to the given list.
     *
     * @param arguments the given list of arguments
     */
    @Override
    public void addArguments(final List<String> arguments) {
        // add all solvents
        for (int i = 0; i < this.types.length; i++) {
            arguments.add("-sol");

            try {
                // if the ratio at i is > 0, add SOLVENTNAME:#RATIO
                if (this.ratios[i] > 0) {
                    arguments.add(this.types[i].toString() + ":" + Integer.toString(this.ratios[i]));
                } else {
                    // otherwise, add only SOLVENTNAME
                    arguments.add(this.types[i].toString());
                }
            } catch (IndexOutOfBoundsException ex) {
                // no ratio at position i, add the solvent without ratio
                arguments.add(this.types[i].toString());
            }
        }

        // add the parameters if given
        if (this.diameter > 0) {
            arguments.add("-sold");
            arguments.add(Double.toString(this.diameter));
        }
        if (this.randomKick != 0) {
            arguments.add("-solr");
            arguments.add(Double.toString(this.randomKick));
        }
        if (this.concentration != 0) {
            arguments.add("-salt");
            arguments.add(Double.toString(this.concentration));
        }
        if (this.charge != 0) {
            arguments.add("-charge");
            arguments.add(Integer.toString(this.charge));
        }
    }

    /**
     * Test if there are only simple solvents given.
     * Simple solvents are not always displayed by JSmol.
     * A random kick of at least 0.4 might help too.
     *
     * @return boolean value whether or not there are only simple solvents
     */
    public boolean onlySimpleSolvents() {
//        // check the random kick
//        if (this.randomKick < 0.4){
//            return 
//        }
        
        // loop over the types
        for (ValidSolventType type : this.types) {
            // if there is one not-simple solvent given, return false
            if (!type.isSimple()) {
                return false;
            }
        }
        return true;
    }
}


