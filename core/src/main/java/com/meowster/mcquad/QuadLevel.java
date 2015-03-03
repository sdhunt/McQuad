/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;
import java.util.Collection;

/**
 * Embodies a set of {@link QuadTile}s at a given zoom level.
 *
 * @author Simon Hunt
 */
public interface QuadLevel {

    /**
     * The name of this level. This will be used as the directory name for
     * the quad tiles. It should include the zoom level.
     *
     * @return the level name
     */
    String name();

    /**
     * Returns the zoom of this level.
     *
     * @return the zoom
     */
    int zoom();

    /**
     * Returns the dimension of this level. That is, the number of
     * {@link QuadTile}s along the width or the height of this level.
     * This should be 2 to the power of the zoom level. For example, if
     * the zoom level is 3, then the dimension will be 2^3 == 8.
     *
     * @return the dimension
     */
    int dim();

    /**
     * Returns the tiles that make up this quad level.
     *
     * @return the tiles in this level
     */
    Collection<QuadTile> tiles();

    /**
     * Returns the quad tile at coordinates [a, b], or null if there is no
     * tile at that location.
     *
     * @param a the a-coord
     * @param b the b-coord
     * @return the tile at [a, b], or null if no such tile
     */
    QuadTile at(int a, int b);

    /**
     * Produces a schematic showing the quad tiling, indicating which
     * tiles have data defined.
     *
     * @return a schematic
     */
    String schematic();

    /**
     * Returns the number of minecraft blocks represented by one "side" of
     * a tile. For example, 2048 would mean from one side of the tile to the
     * other represents a distance of 2048 minecraft blocks.
     *
     * @return the number of blocks along the side of a tile
     */
    int blocksPerTile();

    /**
     * Returns the (quad) coordinates of the tile containing the origin.
     *
     * @return the coordinates of the origin tile
     */
    Coord originTile();

    /**
     * Returns the displacement within the origin tile to locate the true
     * origin. Units are in minecraft blocks. The actual pixel can be computed
     * from number of pixels per tile, {@link #blocksPerTile()} for this level,
     * and the x and z values of the {@link Coord} returned from this method.
     *
     * @return the origin displacement
     */
    Coord originDisplace();

    /**
     * Returns the statistics associated with the building of this level.
     *
     * @return the build statistics
     */
    LevelStats getStats();

    /**
     * Sets the output directory (aka zoom dir) for this level's tiles.
     *
     * @param outputDir the output directory
     */
    void setOutputDir(File outputDir);

    /**
     * Returns the output directory (aka zoom dir) for this level's tiles.
     *
     * @return the output directory
     */
    File outputDir();
}
