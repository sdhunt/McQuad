/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * The partially implemented superclass for quad level builders.
 *
 * @author Simon Hunt
 */
public abstract class QuadLevelBuilder {

    protected static final int BLOCKS_PER_REGION = 512;
    protected static final int BLOCKS_PER_BASE_TILE = 256;
    protected static final Coord ORIGIN = new Coord(0, 0);

    protected final File outputDir;

    /**
     * Constructs the builder.
     *
     * @param outputDir the top level tile output directory
     */
    protected QuadLevelBuilder(File outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * This allows the builder to initialize its internal structures,
     * ready for operation.
     */
    abstract void prepare();

    /**
     * This instructs the builder to create the directory in which the
     * image tiles will be placed.
     */
    abstract void createDirectory();

    /**
     * This instructs the builder to create the image tiles, placing them
     * in the appropriate directory.
     */
    abstract void generateTiles();

    /**
     * Returns the quad level that the builder built; to be used for creating
     * the next level up (zooming out).
     *
     * @return the quad level
     */
    abstract QuadLevel getLevel();

    /**
     * Builder should append related level stats to the given list.
     *
     * @param stats the list to which stats should be added
     */
    abstract void reportStats(List<LevelStats> stats);


    /**
     * Writes the image files to disk.
     *
     * @param ql the quad level
     */
    protected void writeTiles(QuadLevel ql) {
        for (QuadTile tile : ql.tiles()) {
            File pngFile = new File(ql.outputDir(), tile.pngName());
            // remember where we stored the image on disk (for reloading)..
            tile.setLocationOnDisk(pngFile);
            ImageUtils.writeImageToDisk(tile.image(), pngFile);
        }
    }

}
