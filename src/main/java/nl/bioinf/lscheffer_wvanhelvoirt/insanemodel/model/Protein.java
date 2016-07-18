/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 * This class contains the file location of a protein pdb file, and some protein related parameters for insane.py.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public class Protein extends SettingManager {
    /** The pdb file location. */
    private final String fileLocation;
    /** Is it a ring shaped protein. */
    private final boolean ring;
    /** Center the protein along the z axis in the membrane. */
    private final boolean center;
    /** Rotate the protein (princ/random/#angle). */
    private final String rotate;
    /** The protein fudge factor for protein-membrane overlap. */
    private final double fudge;
    /** Give the protein a vertical shift. */
    private final double verticalShift;
    /** The gridsize for the simulation. */
    private final GridSize grid;


    /**
     * Create a new protein given a pdb file + insane parameters.
     *
     * @param errorMessages the list to add error messages to
     * @param fileLocation  the location of the pdb file
     * @param grid          the grid for this simulation
     * @param ring          boolean: is the protein ring shaped?
     * @param center        boolean: center the protein?
     * @param rotate        rotate the protein (princ|random|#angle)
     * @param fudge         the fudge factor for lipid-protein overlap.
     * @param verticalShift the vertical shift of the protein
     */
    public Protein(final List<String> errorMessages,
                   final String fileLocation,
                   final GridSize grid,
                   final boolean ring,
                   final boolean center,
                   final String rotate,
                   final double fudge,
                   final double verticalShift) {
        super(errorMessages);
        this.grid = grid;
        this.fileLocation = fileLocation;
        this.ring = ring;
        this.center = center;
        this.rotate = this.validateRotate(rotate);
        this.fudge = fudge;
        this.verticalShift = verticalShift;
    }

    /**
     * Validate the value of 'rotate', it should be 'random', 'princ' or a number (or empty).
     *
     * @param givenRotate the given rotate value
     * @return            the validated rotate value
     */
    private String validateRotate(final String givenRotate) {
        if (!(givenRotate.equals("") || givenRotate.equals("random") || givenRotate.equals("princ"))) {
            try {
                // if givenRotate is not '', 'random' or 'princ', it should be a number
                Integer.parseInt(givenRotate);
            } catch (NumberFormatException ex) {
                // if it is not a number the given value is invalid
                this.errorMessages.add("Your rotate value '" + givenRotate + "' is not legal and has been removed.");
                return "";
            }
        }
        return givenRotate;
    }

//    /**
//     * Validate the given vertical shift, the protein can not shift further than the minimal vertical distance.
//     *
//     * @param givenVerticalShift the given vertical shift
//     * @return                   the validated vertical shift
//     */
//    private double validateVerticalShift(final double givenVerticalShift) {
//        // if the vertical shift is too big...
//        if (Math.abs(givenVerticalShift) > this.grid.getMinVerticalDistance()) {
//            double newDistance;
//
//            // ...set it to the most extreme value possible...
//            if (givenVerticalShift > 0) {
//                newDistance = this.grid.getMinVerticalDistance();
//            } else {
//                newDistance = -this.grid.getMinVerticalDistance();
//            }
//
//            // ...and add and error message
//            this.errorMessages.add("Your vertical protein shift '" + givenVerticalShift
//                    + "' is too extreme for your grid size, and has been set to '" + newDistance + "'");
//            return newDistance;
//        }
//        return givenVerticalShift;
//    }
    
    public boolean isFileGiven(){
        return !("".equals(this.fileLocation) || this.fileLocation == null);
    }

    /**
     * Add command line arguments of the protein to the given list.
     *
     * @param arguments the given list of arguments
     */
    @Override
    public void addArguments(final List<String> arguments) {
        // no given file means no protein means no arguments
        if (!this.isFileGiven()) {
            return;
        }

        // always add the file location
        arguments.add("-f");
        arguments.add(this.fileLocation);

        // set '-ring' and '-center' if they were true
        if (this.ring) {
            arguments.add("-ring");
        }
        if (this.center) {
            arguments.add("-center");
        }

        // add the parameters if given
        if (!this.rotate.equals("")) {
            arguments.add("-rotate");
            arguments.add(this.rotate);
        }
        if (this.fudge >= 0) {
            arguments.add("-fudge");
            arguments.add(Double.toString(this.fudge));
        }
        if (this.verticalShift != 0) {
            arguments.add("-dm");
            arguments.add(Double.toString(this.verticalShift));
        }

    }
    
    public void addMartinateArguments(final List<String> arguments) {
    }

}
