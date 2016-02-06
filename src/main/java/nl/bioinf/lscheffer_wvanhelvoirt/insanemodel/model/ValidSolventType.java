/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

/**
 * This enum class contains all possible solvent types that can be used in insane.py.
 *
 * @author Lonneke Scheffer
 * @version 1.0
 */
public enum ValidSolventType {
    /** All possible solvent types. */
    W(true), PW, BMW(true), SPC(true), SPCM(true), FG4W, FG4W_MS("FG4W-MS"), GLUC, FRUC, SUCR, MALT, CELL, GLY(true),
    /** All possible solvent types. */
    ALA(true), ASN, ASP, GLU, GLN, LEU, ILE, VAL, SER, THR, CYS, MET, LYS, PRO, HYP, ARG, PHE, TYR, TRP, KOJI, SOPH,
    /** All possible solvent types. */
    NIGE, LAMI, TREH, NA(true), CL(true), MG("Mg", true), K(true), BUT(true);

    /** The string representation of the solvent type. */
    private final String stringRepresentation;
    /** A boolean value that tells whether or not the solvent is 'simple' (JSmol does not show them). */
    private final boolean isSimple;

    /**
     * Create a new ValidSolventType.
     *
     * @param stringRepresentation if the string representation differs from this.name()
     * @param isSimple             boolean that tells whether or not the solvent is 'simple'
     */
    ValidSolventType(final String stringRepresentation, final boolean isSimple) {
        this.stringRepresentation = stringRepresentation;
        this.isSimple = isSimple;
    }

    /**
     * Create a new ValidSolventType.
     *
     * @param stringRepresentation if the string representation differs from this.name()
     */
    ValidSolventType(final String stringRepresentation) {
        this(stringRepresentation, false);
    }

    /**
     * Create a new ValidSolventType.
     *
     * @param isSimple boolean that tells whether or not the solvent is 'simple'
     */
    ValidSolventType(final boolean isSimple) {
        this.stringRepresentation = this.name();
        this.isSimple = isSimple;
    }

    /**
     * Create a new ValidSolventType.
     *
     */
    ValidSolventType() {
        this(false);
    }

    @Override
    public String toString() {
        return this.stringRepresentation;
    }

    /**
     * Tells whether or not the solvent type is 'simple'.
     * Simple solvent types will not be showed by JSmol if there is no
     * non-simple solvent type, lipid, or protein in the same image.
     * This boolean can be used to give the user a warning message.
     *
     * @return boolean whether or not the solvent is simple
     */
    public boolean isSimple() {
        return this.isSimple;
    }
}
