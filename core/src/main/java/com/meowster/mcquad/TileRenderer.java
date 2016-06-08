/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.meowster.util.StringUtils.EOL;

/**
 * Takes quad data and biome/block color data, and renders tiles in the form
 * of .png files, under a set of subdirectories of the output directory.
 *
 * @author Simon Hunt
 */
public class TileRenderer {
    private static final String HEADER      = "Tile Generation Report";
    private static final String HEADER_LINE = "----------------------";

    private final QuadData quadData;
    private final File tilesDir;
    private final QuadLevelBuilderFactory factory;


    private final List<LevelStats> stats = new ArrayList<>();


    /**
     * Creates a tile renderer for the specified quad data, creating tiles
     * under the given directory.
     *
     * @param quadData the quad data
     * @param tilesDir the tiles output directory
     */
    public TileRenderer(QuadData quadData, File tilesDir) {
        this.quadData = quadData;
        this.tilesDir = tilesDir;
        factory = new QuadLevelBuilderFactory(tilesDir);
    }

    /**
     * Does the actual work of rendering the tiles (for all zoom levels).
     */
    public void render() {
        // first, the top level directory, in which we place the zoom dirs.
        prepareOutputDirectory();

        QuadLevelBuilder builder = factory.createBuilder(quadData);
        builder.prepare();
        builder.createDirectory();
        builder.generateTiles();
        builder.reportStats(stats);
        QuadLevel level = builder.getLevel();

        // now progressively zoom out...
        while (level.zoom() > 1) {
            builder = factory.createBuilder(level);
            builder.prepare();
            builder.createDirectory();
            builder.generateTiles();
            builder.reportStats(stats);
            level = builder.getLevel();
        }
    }

    private void prepareOutputDirectory() {
        // For now we'll blow everything away and create all tiles.
        // An enhancement is to only write out tiles that have changed,
        // based on the modification date of the region files.
        PathUtils.deleteTree(tilesDir);
        PathUtils.makeDir(tilesDir);
    }

    /**
     * Returns a string detailing information about the rendering process.
     *
     * @return a report string
     */
    public String report() {
        StringBuilder sb = new StringBuilder(reportHeader());
        for (LevelStats s: stats) {
            sb.append(s).append(EOL);
        }
        sb.append("<end of report summary>").append(EOL);
        return sb.toString();
    }

    private StringBuilder reportHeader() {
        return new StringBuilder(EOL).append(HEADER).append(EOL)
                .append(HEADER_LINE).append(EOL);
    }
}
