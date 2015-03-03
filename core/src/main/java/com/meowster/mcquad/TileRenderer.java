/*
 * Copyright (c) 2014 Meowster.com
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
    private final File outputDir;
    private final QuadLevelBuilderFactory factory;


    private final List<LevelStats> stats = new ArrayList<>();


    /**
     * Creates a tile renderer for the specified quad data, with the given
     * biome and block color databases.
     *
     * @param quadData the quad data
     * @param outputDir the output directory
     */
    public TileRenderer(QuadData quadData, File outputDir) {
        this.quadData = quadData;
        this.outputDir = outputDir;
        factory = new QuadLevelBuilderFactory(outputDir);
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
        QuadLevel level = builder.getLevel();

        // TODO:
/*
        // now progressively zoom out...
        while (level.zoom() > 1) {
            builder = factory.createBuilder(level);
            builder.prepare();
            builder.createDirectory();
            builder.generateTiles();
            level = builder.getLevel();
        }
*/
    }

    private void createZoomDir(QuadLevel level) {
        level.setOutputDir(PathUtils.makeDir(outputDir, level.name()));
    }

    private void prepareOutputDirectory() {
        // For now we'll blow everything away and create all tiles.
        // An enhancement is to only write out tiles that have changed,
        // based on the modification date of the region files.
        PathUtils.deleteTree(outputDir);
        PathUtils.makeDir(outputDir);
    }

    /**
     * Returns a string detailing information about the rendering process.
     *
     * @return a report string
     */
    public String report() {
        return "<end of report summary - TBD>" + EOL;
    }

    private StringBuilder reportHeader() {
        return new StringBuilder(EOL).append(HEADER).append(EOL)
                .append(HEADER_LINE).append(EOL);
    }
}
