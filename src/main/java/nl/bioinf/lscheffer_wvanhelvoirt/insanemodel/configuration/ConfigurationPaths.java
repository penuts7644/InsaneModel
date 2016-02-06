/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.configuration;

/**
 * This class stores all paths to needed folders and files.
 * If you want to use different folders for for instance storing input files, you can change it here.
 * Be very careful when changing paths, and read the description well.
 *
 * @author Lonneke Scheffer
 * @version 1.0.0
 */
public final class ConfigurationPaths {
    /**
     * This is the absolute path to the folder containing insane.py, INSIDE your project.
     * This is also the place where user output files are stored.
     * You can change this path, for instance if your projects folder has a different name, or is at a different
     * location. But it is very important that this path points to a folder INSIDE your project folder.
     *
     * Example: my NetBeansProjects folder is here home/username/IDEs/NetBeansProjects: add "IDEs" before
     * "NetBeansProjects"
     */
    static final private String INSANEFOLDER = String.join(System.getProperty("file.separator"),
            System.getProperty("user.home"), "NetBeansProjects", "InsaneModel", "src", "main", "webapp", "insane");
    /**
     * This is the absolute path to the folder containing insane.py, INSIDE your project.
     * This is the INSANEFOLDER path, with a file separator at the end.
     */
    static final private String INSANEROOT = String.join(System.getProperty("file.separator"), INSANEFOLDER, "");
    /**
     * This is the absolute path to the folder where output files will be stored, INSIDE your project.
     * If you change this path, you also have to change the path WEBOUTFILE so the absolute and relative paths match.
     */
    static final private String OUTFILE = String.join(
            System.getProperty("file.separator"), INSANEFOLDER, "userOutputFiles", "");
    /**
     * This is the relative path to the folder where output files will be stored.
     * It is relative from your project root. If you change this path, you also have to change OUTFILE.
     */
    static final private String WEBOUTFILE = String.join(
            System.getProperty("file.separator"), "insane", "userOutputFiles", "");
    /**
     * This is the absolute path to the folder where input files will be stored, OUTSIDE your project.
     * You can change this path to anything you like, as long as the path exists and is not inside your project folder.
     */
    static final private String INFILE = String.join(System.getProperty("file.separator"),
            System.getProperty("user.home"), "insanemodelfiles", "userInputFiles", "");

    private ConfigurationPaths () {}

    /**
     * Get the root path of the insane folder.
     *
     * @return INSANEROOT, see: {@link ConfigurationPaths#INSANEROOT}
     */
    public static String getRootPath() {
        System.out.println(INSANEROOT);
        System.out.println(INSANEFOLDER);        
        return ConfigurationPaths.INSANEROOT;
    }

    /**
     * Get the path to insane.py.
     *
     * @return path to insane.py
     */
    public static String getPathToInsane() {
        return ConfigurationPaths.INSANEROOT + "insane.py";
    }

    /**
     * Get the absolute input file path, given the name of the file.
     *
     * @param inFile name of the input file
     * @return absolute path to given file
     */
    public static String getAbsoluteInFilePath(String inFile) {
        return ConfigurationPaths.INFILE + inFile;
    }

    /**
     * Get the absolute input file path folder.
     *
     * @return absolute path the folder where input files are stored
     */
    public static String getAbsoluteInFilePath() {
        return ConfigurationPaths.INFILE;
    }

    /**
     * Get the absolute output file path, given the name of the file.
     *
     * @param outFile name of the output file
     * @return absolute path to given file
     */
    public static String getAbsoluteOutFilePath(String outFile) {
        return ConfigurationPaths.OUTFILE + outFile;
    }

    /**
     * Get the absolute output file path folder.
     *
     * @return absolute path the folder where output files are stored
     */
    public static String getAbsoluteOutFilePath() {
        return ConfigurationPaths.OUTFILE;
    }

    /**
     * Get the relative output file path, given the name of the file.
     *
     * @param outFile name of the output file
     * @return relative path to given file
     */
    public static String getWebOutFilePath(String outFile) {
        return ConfigurationPaths.WEBOUTFILE + outFile;
    }

    /**
     * Get the relative output file path folder.
     *
     * @return relative path the folder where output files are stored
     */
    public static String getWebOutFilePath() {
        return ConfigurationPaths.WEBOUTFILE;
    }
}