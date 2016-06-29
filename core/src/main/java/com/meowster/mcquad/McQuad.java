/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.print;
import static com.meowster.util.StringUtils.printOut;
import static java.lang.System.currentTimeMillis;

/**
 * Main class for creating a QuadTiles Map from a Minecraft region DB.
 *
 * @author Simon Hunt
 */
public class McQuad {

    /* This version is displayed in the masthead of the web app */
    private static final String MCQUAD_VERSION = "1.2.2";

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

        long started = currentTimeMillis();

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

        // Load previously saved information about the regions...
        RegionCachedMetaData meta =
                RegionCachedMetaData.load(outputUtils.metaDir());

        // Update with the latest state of the regions...
        meta.update(regionData);

        // project regions into Quad-Space...
        QuadData quad = new QuadData(regionData);
        printOut(quad);
        print(EOL);
//        printOut(quad.schematic());
        boolean flushAll = meta.updateQuadShift(quad.calibration());
        meta.persist();
        printOut(meta);

        // Compute those regions that are stale, then...
        Set<Coord> stale = flushAll ? null : quad.adjust(meta.stale());

        // ...render those tiles that need rendering...
        TileRenderer tr =
                new TileRenderer(quad, outputUtils.tilesDir()).render(stale);
        printOut("\nTOTAL Tiles Rendered: {}", tr.totalTilesRendered());

        // Create today's heatmap
        HeatMapGenerator hmg =
                new HeatMapGenerator(quad.regionsPerSide(), quad.calibration(),
                meta.regionTtlMap(), outputUtils.auxDir());

        // write Javascript parameters to configure zoomable map web app...
        new ParameterizedJs(outputUtils.rootDir())
                .version(MCQUAD_VERSION)
                .baseZoom(quad.baseZoom())
                .heatMapSizePx(hmg.pixels())
                .regionPixel(HeatMapGenerator.PIXELS_PER_REGION)
                .write();

        // TODO: write render report?

        // Are we missing any block definitions from the color file?
        reportDefaultedBlocks();

        printOut(EOL + readableDuration(currentTimeMillis() - started));

        done = true;
        printOut("All Done!");
        return 0;
    }

    private void reportDefaultedBlocks() {
        printOut(EOL + "Defaulted blocks...");
        List<BlockId> defaulted =
                new ArrayList<>(BlockColors.BLOCK_DB.getDefaulted());
        Collections.sort(defaulted);
        if (defaulted.isEmpty()) {
            printOut("  (none)");
        } else {
            for (BlockId id : defaulted) {
                printOut("  " + id);
            }
        }
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
    ParsedArgs parsedArgs() {
        return parsedArgs;
    }

    /**
     * Returns true if we actually completed processing without error.
     *
     * @return true of processing was done
     */
    boolean processingDone() {
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
