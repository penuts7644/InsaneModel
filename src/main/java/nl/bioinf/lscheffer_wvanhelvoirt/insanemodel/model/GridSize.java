/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 * This class validates and stores all grid size related parameters that can be used in insane.py.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public final class GridSize extends SettingManager {
    /** The maximum axis size that will be used to run insane. */
    private static final double MAX_GRID_SIZE = 100;
    /** The maximum axis size that will be used to show output. */
    private static final double MAX_GRID_SIZE_WITH_VIEW = 25;
    /** Distance value for x, y and z axis. */
    private final double d;
    /** The x axis distance. */
    private final double x;
    /** The y axis distance. */
    private final double y;
    /** The z axis distance. */
    private final double z;
    /** Boolean whether to use z or dz (dz = z + protein size). */
    private final boolean dz;
    /** The periodic boundary condition type (hexagonal, rectangular, square, cubic, optimal or keep). */
    private final String pbc;

    /** This boolean tells if the created output is too big to display (>MAX_GRID_SIZE_WITH_VIEW).*/
    private boolean tooBigToDisplay;

    /**
     * Create a new grid size.
     *
     * @param errorMessages the list to add error messages to
     * @param d             distance for x, y, and z if they are the same
     * @param x             distance for x axis
     * @param y             distance for y axis
     * @param z             distance for z axis
     * @param dz            boolean whether to use z or z + protein size
     * @param pbc           the periodic boundary condition type
     */
    public GridSize(final List<String> errorMessages,
                    final double d,
                    final double x,
                    final double y,
                    final double z,
                    final boolean dz,
                    final String pbc) {
        super(errorMessages);
        this.tooBigToDisplay = false;
        this.d = this.validateAnyDistance(Math.abs(d), 10);
        this.x = this.validateAnyDistance(Math.abs(x), this.d);
        this.y = this.validateAnyDistance(Math.abs(y), this.d);
        this.z = this.validateAnyDistance(Math.abs(z), this.d);
        this.dz = dz;
        this.pbc = pbc;
    }

    /**
     * Validate the given distance size.
     * The distance can not be bigger than MAX_GRID_SIZE,
     * and an error message is given if the distance is bigger than MAX_GRID_SIZE_WITH_VIEW.
     *
     * @param givenDistance the given distance value
     * @param defaultValue  the default value to return if no distance value is given
     * @return              a valid distance value
     */
    private double validateAnyDistance(final double givenDistance, final double defaultValue){
        String errorMessage = "";

        if ((givenDistance) == 0) {
            return defaultValue;
        } else if (givenDistance > GridSize.MAX_GRID_SIZE_WITH_VIEW) {
            this.tooBigToDisplay = true;

            errorMessage += "Note that grid size values above " + GridSize.MAX_GRID_SIZE_WITH_VIEW 
                    + " will not be displayed by JSmol, there is only a downloadable output file available.";

            if (givenDistance > GridSize.MAX_GRID_SIZE) {
                errorMessage = "Given grid size '" + givenDistance + "' is too big and has been set to "
                            + GridSize.MAX_GRID_SIZE + errorMessage + ". ";
                this.errorMessages.add(errorMessage);
                return GridSize.MAX_GRID_SIZE;
            }
            this.errorMessages.add(errorMessage);
        }
        return givenDistance;
    }
//
//    /**
//     * Validate the given distance size.
//     *
//     * @param givenD       the given distance value
//     * @param defaultValue the default value to return if no distance value is given
//     * @return             a valid distance value
//     */
//    private double validateD(final double givenD, final double defaultValue) {
//        if ((givenD) == 0) {
//            return defaultValue;
//        } else if (givenD > GridSize.MAXGRIDSIZE) {
//            // given d value is too big, set to MAXGRIDSIZE and add an error message
//            errorMessages.add("Given grid size '" + givenD + "' is too big and has been set to "
//                            + GridSize.MAXGRIDSIZE + ".");
//            return GridSize.MAXGRIDSIZE;
//        } else {
//            return givenD;
//        }
//    }
//
//    /**
//     * Validate the given x, y or z axis distance.
//     *
//     * @param givenXyz the given x, y or z axis distance
//     * @return         a valid x, y or z axis distance
//     */
//    private double validateXyz(final double givenXyz) {
//        if ((givenXyz) == 0) {
//            // if no x, y or z value is given, return the default d value
//            return this.d;
//        } else if (givenXyz > GridSize.MAXGRIDSIZE) {
//            // given axis value is too big, set to MAXGRIDSIZE and add an error message
//            errorMessages.add("Given axis size '" + givenXyz + "' is too big and has been set to"
//                    + GridSize.MAXGRIDSIZE + ".");
//            return GridSize.MAXGRIDSIZE;
//        } else {
//            return givenXyz;
//        }
//    }

    /**
     * Get the minimal vertical distance.
     * This is the z axis distance.
     *
     * @return the minimal vertical distance
     */
    public double getMinVerticalDistance() {
        return this.z;
    }

    /**
     * Get the minimal horizontal distance.
     * This is the minimum value of the x and y value.
     *
     * @return the minimal horizontal
     */
    public double getMinHorizontalDistance() {
        return Math.min(this.x, this.y);
    }

    /**
     * Add command line arguments of the grid size to the given list.
     *
     * @param arguments the given list of arguments
     */
    @Override
    public void addArguments(final List<String> arguments) {
        // always add the -d value
        arguments.add("-d");
        arguments.add(Double.toString(this.d));

        // if a unique x, y or z axis is given, add it
        if (this.x != this.d) {
            arguments.add("-x");
            arguments.add(Double.toString(this.x));
        }
        if (this.y != this.d) {
            arguments.add("-y");
            arguments.add(Double.toString(this.y));
        }

        // always add z, to make the difference between dz and z
        // if this.dz is set to true, use -dz (z = z + protein size)
        if (this.dz) {
            arguments.add("-dz");
        } else {
            // otherwise, use -z (z = z)
            arguments.add("-z");
        }
        arguments.add(Double.toString(this.z));


        // if a -pbc value is given, add it
        if (!this.pbc.equals("")) {
            arguments.add("-pbc");
            arguments.add(this.pbc);
        }

    }
    
    
    public void addMartinateArguments(final List<String> arguments) {
    }

    /**
     * Checks if the given grid size is 'too big' for JSmol to view.
     *
     * @return boolean whether grid is larger than MAX_GRID_SIZE_WITH_VIEW
     */
    public boolean isTooBig() {
        return this.tooBigToDisplay;
    }
}
