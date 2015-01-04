/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import static com.meowster.util.StringUtils.printOut;

/**
 * Main class for creating a QuadTiles Map from a Minecraft region DB.
 *
 * @author Simon Hunt
 */
public class McQuad {

    private final ParsedArgs parsedArgs;

    private boolean done;

    // package private for unit tests access too.
    McQuad(ParsedArgs parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    /**
     * Main process method which executes the program. Returns the
     * appropriate system exit code.
     *
     * @return the system exit code
     */
    int process() {
        printOut("McQuad!");

        if (parsedArgs.showHelp())
            return 0;


        RegionData regionData;
        try {
            regionData = new RegionData().load(parsedArgs.regionDir);
            printOut(regionData);

        } catch (Exception e) {
            printOut(e);
            parsedArgs.printUsage();
            return 1;
        }

        QuadData quad = new QuadData(regionData);
        printOut(quad);
        printOut(quad.schematic());

        // FIXME: needs to be fixed
//        TileRenderer tr = new TileRenderer(quad, parsedArgs.outputDir);
//        tr.render();
//        printOut(tr.report());

        done = true;
        printOut("All Done!");
        return 0;
    }

    /**
     * Returns a reference to our parsed arguments.
     *
     * @return the parsed args
     */
    public ParsedArgs parsedArgs() {
        return parsedArgs;
    }

    /**
     * Returns true if we actually completed processing without error.
     *
     * @return true of processing was done
     */
    public boolean processingDone() {
        return done;
    }

    /**
     * Main launch point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.exit(new McQuad(ParsedArgs.build(args)).process());
    }
}
