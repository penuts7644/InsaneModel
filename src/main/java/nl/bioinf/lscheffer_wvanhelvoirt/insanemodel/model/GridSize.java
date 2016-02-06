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
 * @version 1.0
 */
public class GridSize {
    /** The maximum axis size. */
    private static final double MAXGRIDSIZE = 25;
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

    /** The list to add error messages to. */
    private final List<String> errorMessages;

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
        this.errorMessages = errorMessages;
        this.d = this.validateD(Math.abs(d), 10);
        this.x = this.validateXyz(Math.abs(x));
        this.y = this.validateXyz(Math.abs(y));
        this.z = this.validateXyz(Math.abs(z));
        this.dz = dz;
        this.pbc = pbc;
    }

    /**
     * Validate the given distance size.
     *
     * @param givenD       the given distance value
     * @param defaultValue the default value to return if no distance value is given
     * @return             a valid distance value
     */
    private double validateD(final double givenD, final double defaultValue) {
        if ((givenD) == 0) {
            return defaultValue;
        } else if (givenD > GridSize.MAXGRIDSIZE) {
            // given d value is too big, set to MAXGRIDSIZE and add an error message
            errorMessages.add("Given grid size '" + givenD + "' is too big and has been set to "
                            + GridSize.MAXGRIDSIZE + ".");
            return GridSize.MAXGRIDSIZE;
        } else {
            return givenD;
        }
    }

    /**
     * Validate the given x, y or z axis distance.
     *
     * @param givenXyz the given x, y or z axis distance
     * @return         a valid x, y or z axis distance
     */
    private double validateXyz(final double givenXyz) {
        if ((givenXyz) == 0) {
            // if no x, y or z value is given, return the default d value
            return this.d;
        } else if (givenXyz > GridSize.MAXGRIDSIZE) {
            // given axis value is too big, set to MAXGRIDSIZE and add an error message
            errorMessages.add("Given axis size '" + givenXyz + "' is too big and has been set to"
                    + GridSize.MAXGRIDSIZE + ".");
            return GridSize.MAXGRIDSIZE;
        } else {
            return givenXyz;
        }
    }

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
        if (this.z != this.d) {
            // if this.dz is set to true, use -dz (z = z + protein size)
            if (this.dz) {
                arguments.add("-dz");
            } else {
                // otherwise, use -z (z = z)
                arguments.add("-z");
            }
            arguments.add(Double.toString(this.z));
        }

        // if a -pbc value is given, add it
        if (!this.pbc.equals("")) {
            arguments.add("-pbc");
            arguments.add(this.pbc);
        }

    }
}
