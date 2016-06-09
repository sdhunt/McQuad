/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;

import static com.meowster.util.StringUtils.printOut;

/**
 * Create and process command line args.
 *
 * @author Simon Hunt
 */
class ParsedArgs {
    private static final char DASH = '-';
    private static final String _O = "-o";
    private static final String _HELP = "-help";
    private static final String _QH = "-?";

    private static final String USAGE_ARGS = "<region-dir> -o <output-dir>";

    private boolean showHelp = false;
    File regionDir;
    File outputDir;

    @Override
    public String toString() {
        return "{Args: showHelp=" + showHelp +
                ", regionDir=" + regionDir +
                ", outputDir=" + outputDir +
                "; VALID=" + valid() +
                "}";
    }

    /**
     * Builds an args instance from the given command line arguments.
     *
     * @param args the command line arguments
     * @return an args instance
     */
    public static ParsedArgs build(String... args) {
        ParsedArgs pa = new ParsedArgs();
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.length() > 0 && s.charAt(0) != DASH) {
                pa.regionDir = new File(s);
            } else {
                switch (s) {
                    case _O:
                        pa.outputDir = new File(args[++i]);
                        break;

                    case _HELP:
                    case _QH:
                    default:
                        pa.showHelp = true;
                        break;
                }
            }
        }
        return pa;
    }

    /**
     * If the help option was supplied, the usage message is printed and
     * this method returns true, otherwise false is returned.
     *
     * @return true if help was requested
     */
    boolean showHelp() {
        if (showHelp)
            printUsage();
        return showHelp;
    }

    void printUsage() {
        printOut("Usage: {} {}", McQuad.class.getName(), USAGE_ARGS);
    }

    /**
     * Returns true if the supplied parameters are good for processing.
     * That is to say, the specified region must be an existing directory
     * and the output must either not exist, or must be an existing directory.
     * Oh, and the two directories cannot be the same.
     *
     * @return true if these parameters would be good for processing
     */
    boolean valid() {
        if (regionDir == null || outputDir == null)
            return false;

        if (regionDir.equals(outputDir))
            return false;

        boolean regOk = regionDir.isDirectory();
        boolean outOk = (!outputDir.exists() || outputDir.isDirectory());
        return regOk && outOk;
    }
}
