/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 * This class represents a lipid that can be used in insane.py.
 * The class must have a valid lipid type, and has an upper- and lower membrane leaflet lipid ratio.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public class StandardLipid {
    /** The type of the standard lipid. */
    private final ValidLipidType type;
    /** The ratio of the lipids in the upper membrane leaflet. */
    private final int upperLeafletRatio;
    /** The ratio of the lipids in the lower membrane leaflet. */
    private final int lowerLeafletRatio;

    /**
     * Create a new StandardLipid.
     *
     * @param type              the valid lipid type
     * @param upperLeafletRatio the upper membrane leaflet ratio
     * @param lowerLeafletRatio the lower membrane leaflet ratio
     */
    public StandardLipid(final ValidLipidType type,
                         final int upperLeafletRatio,
                         final int lowerLeafletRatio) {
        this.type = type;
        this.upperLeafletRatio = upperLeafletRatio;
        this.lowerLeafletRatio = lowerLeafletRatio;
    }

    /**
     * Add command line arguments of the StandardLipidType to the given list.
     *
     * @param arguments the given list of arguments
     */
    public void addArguments(final List<String> arguments) {
        // add lower leaflet lipids
        if (this.lowerLeafletRatio > 0) {
            arguments.add("-l");
            arguments.add(this.type.toString() + ":" + this.lowerLeafletRatio);
        }

        // add upper leaflet lipids
        if (this.upperLeafletRatio > 0) {
            arguments.add("-u");
            arguments.add(this.type.toString() + ":" + this.upperLeafletRatio);
        }
    }

    /**
     * Tells whether or not the lipid type of this StandardLipid is 'simple'.
     * Simple lipids types will not be showed by JSmol if there is no
     * non-simple solvent type, lipid, or protein in the same image.
     * This boolean can be used to give the user a warning message.
     *
     * @return boolean whether or not the lipid is simple
     */
    public boolean isSimple() {
        return this.type.isSimple();
    }
}
