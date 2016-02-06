/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 * This class represents the membrane and all membrane related parameters that can be set in insane.py.
 * It contains arrays of standard lipids and additional lipids, and validates the parameters that might cause an exception in insane.
 *
 * @author Lonneke Scheffer
 * @version 1.0
 */
public class Membrane {
    /** The array of StandardLipids. */
    private final StandardLipid[] standardLipids;
    /** The array of AdditionalLipids. */
    private final AdditionalLipid[] additionalLipids;
    /** The given GridSize for this simulation. */
    private final GridSize grid;
    /** The given random kick. */
    private final double randomKick;
    /** The ratio of lipids in the upper membrane leaflet. */
    private final double areaUpperLipids;
    /** The ratio of lipids in the lower membrane leaflet. */
    private final double areaLowerLipids;
    /** The amount of asymmetric lipids in the membrane. */
    private final int membraneAsymmetry;
    /** The given hole size. */
    private final double hole;
    /** The given disc size. */
    private final double disc;
    /** The distance between the membrane beads. */
    private final double beadDistance;

    /** The list to add error messages to. */
    private final List<String> errorMessages;

    /**
     * Create a new Membrane.
     *
     * @param errorMessages     the list to add error messages to
     * @param standardLipids    the array of standard lipids
     * @param additionalLipids  the array of additional lipids
     * @param grid              the grid for this simulation
     * @param randomKick        the random kick
     * @param areaLowerLipids   the ratio of lipids in the lower membrane leaflet
     * @param areaUpperLipids   the ratio of lipids in the upper membrane leaflet
     * @param membraneAsymmetry the amount of asymmetric lipids in the membrane
     * @param hole              the membrane hole size
     * @param disc              the membrane disc size
     * @param beadDistance      the distance between the membrane beads
     */
    public Membrane(final List<String> errorMessages,
                    final StandardLipid[] standardLipids,
                    final AdditionalLipid[] additionalLipids,
                    final GridSize grid,
                    final double randomKick,
                    final double areaLowerLipids,
                    final double areaUpperLipids,
                    final int membraneAsymmetry,
                    final double hole,
                    final double disc,
                    final double beadDistance) {
        this.errorMessages = errorMessages;
        this.standardLipids = standardLipids;
        this.additionalLipids = additionalLipids;
        this.grid = grid;
        this.randomKick = this.validateRandomKick(randomKick);
        this.areaLowerLipids = areaLowerLipids;
        this.areaUpperLipids = areaUpperLipids;
        this.membraneAsymmetry = membraneAsymmetry;
        this.hole = hole;
        this.disc = disc;
        this.beadDistance = this.validateBeadDistance(beadDistance);
    }

    /**
     * Validate the given random kick.
     *
     * @param userRandomKick the given random kick
     * @return               the validated random kick
     */
    private double validateRandomKick(final double userRandomKick) {
        // the random kick can not be higher than half of the minimal horizontal distance
        // a bigger random kick results in an error in insane
        if (Math.abs(userRandomKick) > (this.grid.getMinHorizontalDistance() / 2)) {
            this.errorMessages.add("Random kick '" + userRandomKick
                    + "' is too extreme for the given grid size, and has been set to '"
                    + (this.grid.getMinHorizontalDistance() / 2) + "'");
            // if someone tries a big random kick, return the maximum random kick possible.
            return this.grid.getMinHorizontalDistance() / 2;
        }
        return userRandomKick;
    }

    /**
     * Validate the given bead distance.
     *
     * @param userBeadDistance the given bead distance
     * @return                 the validated bead distance
     */
    private double validateBeadDistance(final double userBeadDistance) {
        // the bead distance can not be higher than one fourth of the minimal vertical distance
        // a bigger bead distance results in an error in insane
        if (Math.abs(userBeadDistance) > (this.grid.getMinVerticalDistance() / 4)) {
            this.errorMessages.add("Bead distance '" + userBeadDistance
                    + "' is too extreme for the given grid size, and has been set to '"
                    + (this.grid.getMinVerticalDistance() / 4) + "'");
            // if someone tries a big bead distance, return the maximum bead distance possible
            return this.grid.getMinVerticalDistance() / 4;
        }
        return userBeadDistance;
    }

    /**
     * Test if there are only simple lipids given.
     * Simple lipids are not always displayed by JSmol.
     *
     * @return boolean value whether or not there are only simple lipids
     */
    public boolean onlySimpleLipids() {
        // loop over all standard lipids
        for (StandardLipid standardLipid : this.standardLipids) {
            // if there is one not-simple lipid found, return false
            if (!standardLipid.isSimple()) {
                return false;
            }
        }

        // loop over all additional lipids
        for (AdditionalLipid additionalLipid : this.additionalLipids) {
            if (!additionalLipid.isSimple()) {
                return false;
            }
        }

        return true;
    }


    /**
     * Add command line arguments of the membrane to the given list.
     *
     * @param arguments the given list of arguments
     */
    public void addArguments(final List<String> arguments) {
        // if there are no lipids, there is no membrane.
        // add only '-excl -1' so the solvent can be in the place of the membrane
        if (this.standardLipids.length == 0 && this.additionalLipids.length == 0) {
            arguments.add("-excl");
            arguments.add("-1");
            return;
        }

        // add the arguments of all standard lipids and additional lipids
        for (StandardLipid lipid : this.standardLipids) {
            lipid.addArguments(arguments);
        }
        for (AdditionalLipid lipid : this.additionalLipids) {
            lipid.addArguments(arguments);
        }

        // add the arguments of all other variables if they are given (> 0)
        if (this.randomKick > 0) {
            arguments.add("-rand");
            arguments.add(Double.toString(this.randomKick));
        }
        if (this.areaLowerLipids > 0) {
            arguments.add("-a");
            arguments.add(Double.toString(this.areaLowerLipids));
        }
        if (this.areaUpperLipids > 0) {
            arguments.add("-au");
            arguments.add(Double.toString(this.areaUpperLipids));
        }
        // not >0 but != 0 because membraneAssymetry can be set to a negative number
        if (this.membraneAsymmetry != 0) {
            arguments.add("-asym");
            arguments.add(Integer.toString(this.membraneAsymmetry));
        }
        if (this.hole > 0) {
            arguments.add("-hole");
            arguments.add(Double.toString(this.hole));
        }
        if (this.disc > 0) {
            arguments.add("-disc");
            arguments.add(Double.toString(this.disc));
        }
        if (this.beadDistance > 0) {
            arguments.add("-bd");
            arguments.add(Double.toString(this.beadDistance));
        }

    }

}
