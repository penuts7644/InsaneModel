/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

/**
 * This enum class contains all possible lipid types that can be used in insane.py.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public enum ValidLipidType {
    /** All possible solvent types. */
    DTPC, DLPC, DPPC, DBPC, POPC, DOPC, DAPC, DIPC, DGPC, DNPC, DTPE, DLPE, DPPE, DBPE, POPE, DOPE, POPG, DOPG, POPS,
    /** All possible solvent types. */
    DOPS, DPSM, DBSM, BNSM, OPPG, JPPG, JFPG, GMO(true), DPPI, POPI, PIPI, PAPI, PUPI, POP1, POP2, POP3, DPG1, DXG1,
    /** All possible solvent types. */
    PNG1, XNG1, DPG3, DXG3, PNG3, XNG3, DPCE, DPGS, DPMG, DPSG, DPGG, OPMG, OPSG, OPGG, FPMG, DFMG, FPSG, FPGG, DFGG,
    /** All possible solvent types. */
    PLQ, CDL0, CDL1, CDL2, CL4P, CL4M, AMA, KMA, MMA, CHOL(true);

    /** A boolean value that tells whether or not the lipid is 'simple' (JSmol does not show them). */
    private final Boolean isSimple;

    /**
     * Create a new ValidLipidType.
     *
     * @param isSimple whether or not the lipid is 'simple'
     */
    ValidLipidType(final Boolean isSimple) {
        this.isSimple = isSimple;
    }
    /**
     * Create a new ValidLipidType.
     *
     */
    ValidLipidType() {
        this(false);
    }

    /**
     * Tells whether or not the lipid type is 'simple'.
     * Simple lipids types will not be showed by JSmol if there is no
     * non-simple solvent type, lipid, or protein in the same image.
     * This boolean can be used to give the user a warning message.
     *
     * @return boolean whether or not the solvent is simple
     */
    public boolean isSimple() {
        return this.isSimple;
    }
}
