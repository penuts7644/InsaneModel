/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents an additional lipid type (user-created lipid) that can be used in insane.py.
 * The user input (headgroups, linkergroups and tailgroups) is validated so insane doesn crash.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public class AdditionalLipid {
    /** The AdditionalLipids are counted to create unique names. */
    private static int count = 0;
    /** The set of valid head groups. */
    private static final Set<String> VALID_HEAD = new HashSet();
    /** The set of valid linker groups. */
    private static final Set<String> VALID_LINKER = new HashSet();
    /** The set of valid tail characters. */
    private static final Set<String> VALID_TAIL = new HashSet();
    static {
        VALID_HEAD.add("");
        VALID_HEAD.add("C");
        VALID_HEAD.add("E");
        VALID_HEAD.add("G");
        VALID_HEAD.add("S");
        VALID_HEAD.add("P");
        VALID_LINKER.add("A");
        VALID_LINKER.add("G");
        VALID_TAIL.add("C");
        VALID_TAIL.add("D");
        VALID_TAIL.add("T");
    }

    /** The unique name of the created lipid. */
    private final String name;
    /** The array of head groups. */
    private final String[] headGroups;
    /** The array of linker groups. */
    private final String[] linkerGroups;
    /** The array of tail groups. */
    private final String[] tailGroups;
    /** The ratio of lipids in the upper membrane leaflet. */
    private final int upperLeafletRatio;
    /** The ratio of lipids in the lower membrane leaflet. */
    private final int lowerLeafletRatio;

    /** The list to add error messages to. */
    private final List<String> errorMessages;

    /**
     * Create a new AdditionalLipid given the head-, linker- and tailgroups, and the membrane leaflet ratios.
     *
     * @param errorMessages     the list to add error messages to
     * @param headGroups        the array of head groups
     * @param linkerGroups      the array of linker groups
     * @param tailGroups        the array of tail groups
     * @param lowerLeafletRatio the ratio in the upper membrane leaflet
     * @param upperLeafletRatio the ratio in the lower membrane leaflet
     */
    public AdditionalLipid(final List<String> errorMessages,
                           final String[] headGroups,
                           final String[] linkerGroups,
                           final String[] tailGroups,
                           final int upperLeafletRatio,
                           final int lowerLeafletRatio) {
        AdditionalLipid.count++;
        this.name = this.defineName();
        this.errorMessages = errorMessages;
        this.headGroups = this.validateHeadGroups(headGroups);
        this.linkerGroups = this.validateLinkerGroups(linkerGroups);
        this.tailGroups = this.validateTailGroups(tailGroups);
        this.lowerLeafletRatio = lowerLeafletRatio;
        this.upperLeafletRatio = upperLeafletRatio;
    }

    /**
     * Resets the counter that is used to create the lipid name.
     *
     */
    public static void resetCounter() {
        AdditionalLipid.count = 0;
    }

    /**
     * Define the unique lipid name.
     *
     * @return lipid name
     */
    private String defineName() {
        return "LIP" + AdditionalLipid.count;
    }

    /**
     * Validate the given head groups, change the given head groups to make them valid if necessary.
     *
     * @param userHeadGroups the given head groups
     * @return               the validated head groups
     */
    private String[] validateHeadGroups(final String[] userHeadGroups) {
        String headGroup;

        // loop through the given head groups
        for (int i = 0; i < userHeadGroups.length; i++) {
            headGroup = userHeadGroups[i];
            if (!VALID_HEAD.contains(headGroup)) {
                // if the found head group is not valid, set it to an empty string and add an error message
                this.errorMessages.add("Your lipid '" + this.defineName() + "' contains invalid headgroup '"
                        + headGroup + "', which has been removed.");
                userHeadGroups[i] = "";
            }
        }

        return userHeadGroups;
    }

    /**
     * Validate the given linker groups, change the given linker groups to make them valid if necessary.
     *
     * @param userLinkerGroups the given linker groups
     * @return                 the validated linker groups
     */
    private String[] validateLinkerGroups(final String[] userLinkerGroups) {
        String linkerGroup;

        // loop through the linker groups
        for (int i = 0; i < userLinkerGroups.length; i++) {
            linkerGroup = userLinkerGroups[i];
            if (!VALID_LINKER.contains(linkerGroup)) {
                // if the found linker group is not valid, set it to 'G' and add an error message
                // (do not remove it because the amount of tail groups and linker groups should be the same)
                this.errorMessages.add("Your lipid '" + this.defineName() + "' contains invalid linker group '"
                        + linkerGroup + "', which has been replaced by 'G'.");
                userLinkerGroups[i] = "G";
            }
        }

        return userLinkerGroups;
    }

    /**
     * Validate the given tail groups, change the given tail groups to make them valid if necessary.
     * The amount of tail groups should be equal to the amount of linker groups.
     *
     * @param userTailGroups the given tail groups
     * @return               the validated tail groups
     */
    private String[] validateTailGroups(final String[] userTailGroups) {
        String[] newTailGroups = new String[this.linkerGroups.length];
        String tailBead;

        // loop through the tail groups lenght (THIS IS THE FOUND LINKER LENGTH)
        for (int i = 0; i < newTailGroups.length; i++) {
            // try to put the found tail group in a string array
            try {
                // test if the tails array is not empty (possible if the user gave an empty string as 'tail groups')
                if ("".equals(userTailGroups[i])) {
                    this.errorMessages.add("Your lipid '" + this.defineName() + "' contains an empty tail group, a tail"
                            + " group containing only 'C' has been added.");
                    newTailGroups[i] = "C";
                } else {
                    newTailGroups[i] = userTailGroups[i];
                }
            } catch (IndexOutOfBoundsException ex) {
                // if it does not work (because there was no tail group given at position i):
                // add an error message and set the new tail group to 'C'
                this.errorMessages.add("Your lipid '" + this.defineName() + "' contains more linkers than tails, a tail"
                        + " containing only 'C' has been added.");
                newTailGroups[i] = "C";
            }
        }

        // if there were more tail groups than linker groups given, add an error message
        if (userTailGroups.length > newTailGroups.length) {
            this.errorMessages.add("Your lipid '" + this.defineName() + "' contains more tails than linkers, some tails"
                    + " have been removed.");
        }

        // validate the tail group beads
        // loop through the tail groups
        for (String tailGroup : newTailGroups) {
            // otherwise: loop through the tail beads per group
            for (int i = 0; i < tailGroup.length(); i++) {
                // check if the tail bead is valid, add an error message if not, and set the bead to 'C'
                tailBead = tailGroup.charAt(i) + "";
                if (!VALID_TAIL.contains(tailBead)) {
                    this.errorMessages.add("Your lipid '" + this.defineName() + "' contains invalid tail bead '"
                            + tailBead + "', which has been replaced by 'C'");
                    tailGroup = tailGroup.replace(tailBead, "C");
                }
            }
        }

        return newTailGroups;
    }

    /**
     * Tells whether or not this AdditionalLipid is 'simple'.
     * Simple lipids types will not be showed by JSmol if there is no
     * non-simple solvent type, lipid, or protein in the same image.
     * This boolean can be used to give the user a warning message.
     *
     * @return boolean whether or not the lipid is simple
     */
    public boolean isSimple() {
        return String.join("", this.headGroups).length() == 0;
    }

    /**
     * Add command line arguments of the AdditionalLipid to the given list.
     *
     * @param arguments the given list of arguments
     */
    public void addArguments(final List<String> arguments) {
        if (this.headGroups.length == 0 && this.linkerGroups.length == 0 && this.tailGroups.length == 0) {
            // the lipid has no headgroups, linker groups or tail groups: don't add it
            return;
        }

        // add the head, tail and linker groups
        arguments.add("-alhead");
        arguments.add(String.join(" ", this.headGroups));

        arguments.add("-allink");
        arguments.add(String.join(" ", this.linkerGroups));

        arguments.add("-altail");
        arguments.add(String.join(" ", this.tailGroups));

        // add the unique name
        arguments.add("-alname");
        arguments.add(this.name);

        arguments.add("-l");
        // if there is no valid ratio given, add no ratio
        if (this.lowerLeafletRatio <= 0) {
            arguments.add(this.name);
        } else {
            // if there is a ratio given (> 0), add it (NAME:#RATIO)
            arguments.add(this.name + ":" + this.lowerLeafletRatio);
        }

        // if there is an upper leaflet ratio given, add it
        if (this.upperLeafletRatio > 0) {
            arguments.add("-u");
            arguments.add(this.name + ":" + this.upperLeafletRatio);
        }
    }
}
