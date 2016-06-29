/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.printOut;

/**
 * Takes quad data and biome/block color data, and renders tiles in the form
 * of .png files, under a set of subdirectories of the output directory.
 *
 * @author Simon Hunt
 */
class TileRenderer {
    private static final String HEADER = "Tile Generation Report";
    private static final String HEADER_LINE = "----------------------";

    private final QuadData quadData;
    private final File tilesDir;
    private final QuadLevelBuilderFactory factory;


    private final List<LevelStats> stats = new ArrayList<>();

    private long totalTilesRendered = 0L;


    /**
     * Creates a tile renderer for the specified quad data, creating tiles
     * under the given directory.
     *
     * @param quadData the quad data
     * @param tilesDir the tiles output directory
     */
    TileRenderer(QuadData quadData, File tilesDir) {
        this.quadData = quadData;
        this.tilesDir = tilesDir;
        factory = new QuadLevelBuilderFactory(tilesDir);
    }

    /**
     * Does the actual work of rendering the tiles (for all zoom levels), for
     * the given quad coordinates.
     *
     * @param stale coordinates of stale regions
     */
    TileRenderer render(Set<Coord> stale) {
        if (stale == null) {
            printOut("Calibration change detected -- regenerating all tiles!");
            PathUtils.deleteTree(tilesDir);
        }
        PathUtils.createIfNeedBe(tilesDir);

        if (stale != null && stale.isEmpty()) {
            printOut("No stale regions -- nothing to do!!");
            return this;
        }

        QuadLevelBuilder builder = factory.createBuilder(quadData);
        builder.prepare(stale);
        builder.createDirectory();
        totalTilesRendered += builder.generateTiles();
        builder.saveStats(stats);
        QuadLevel level = builder.getLevel();
        Set<Coord> zoomedOutStale = builder.zoomedOutStale();

        // now progressively zoom out...
        while (level.zoom() > 1) {
            builder = factory.createBuilder(level);
            builder.prepare(zoomedOutStale);
            builder.createDirectory();
            totalTilesRendered += builder.generateTiles();
            builder.saveStats(stats);
            level = builder.getLevel();
            zoomedOutStale = builder.zoomedOutStale();
        }
        return this;
    }

    /**
     * Returns a string detailing information about the rendering process.
     *
     * @return a report string
     */
    String report() {
        StringBuilder sb = new StringBuilder(reportHeader());
        for (LevelStats s : stats) {
            sb.append(s).append(EOL);
        }
        sb.append("<end of report summary>").append(EOL);
        return sb.toString();
    }

    private StringBuilder reportHeader() {
        return new StringBuilder(EOL).append(HEADER).append(EOL)
                .append(HEADER_LINE).append(EOL);
    }

    /**
     * Returns the total number of tiles rendered by the renderer.
     *
     * @return number of tiles rendered
     */
    long totalTilesRendered() {
        return totalTilesRendered;
    }
}
