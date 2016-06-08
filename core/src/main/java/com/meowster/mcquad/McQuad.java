/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.util.concurrent.TimeUnit;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.print;
import static com.meowster.util.StringUtils.printOut;

/**
 * Main class for creating a QuadTiles Map from a Minecraft region DB.
 *
 * @author Simon Hunt
 */
public class McQuad {

    private final ParsedArgs parsedArgs;
    private final OutputUtils outputUtils;

    private boolean done;

    // package private for unit tests access too.
    McQuad(ParsedArgs parsedArgs) {
        this.parsedArgs = parsedArgs;
        outputUtils = new OutputUtils(parsedArgs.outputDir);
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

        long started = System.currentTimeMillis();

        RegionData regionData;
        try {
            regionData = new RegionData().load(parsedArgs.regionDir);
            printOut(regionData);

        } catch (Exception e) {
            printOut(e);
            parsedArgs.printUsage();
            return 1;
        }

        // At this point we have created a region model for the world.

        // We need to load region metadata information, so we know which
        //  regions have been modified since last we generated the tiles.
        RegionCachedMetaData meta =
                RegionCachedMetaData.load(outputUtils.metaDir());
        printOut(meta);
        // TODO: once we have this, need to pass it to tile renderer

        // Now we need to project the regions into a Quad format.

        QuadData quad = new QuadData(regionData);
        printOut(quad);
        print(EOL);
        printOut(quad.schematic());

        // Parameterize the Javascript
        new ParameterizedJs(outputUtils.rootDir(), quad.maxZoom() + 1);

        printOut("Regions to process: " + regionData.regions().size());

        TileRenderer tr = new TileRenderer(quad, outputUtils.tilesDir());
        tr.render();

        long duration = System.currentTimeMillis() - started;
        printOut(EOL + readableDuration(duration));

        done = true;
        printOut("All Done!");
        return 0;
    }

    private String readableDuration(long ms) {
        long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
        long minsAsSecs = TimeUnit.MINUTES.toSeconds(mins);
        long secs = TimeUnit.MILLISECONDS.toSeconds(ms) - minsAsSecs;
        return String.format("Duration: %d minutes, %d seconds", mins, secs);
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
