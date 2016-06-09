/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a tile in a quad map.
 *
 * @author Simon Hunt
 */
interface QuadTile {

    /**
     * Returns the tile image.
     *
     * @return the tile image
     */
    BufferedImage image();

    /**
     * Returns the coordinates of the tile with respect to the quad level:
     * <pre>
     *     [a, b] where a and b are each in the range {0 .. N-1} given that
     *      N is the dimension of the quad level
     * </pre>
     *
     * @return the coordinates
     */
    Coord coord();

    /**
     * Instructs the implementation to release the resources used to hold
     * the raw data and image data.
     */
    void releaseResources();

    /**
     * Returns the name of the subdirectory for the x-coord of the tile.
     *
     * @return x subdirectory name
     */
    String xDirName();

    /**
     * Returns the file name for the tile image (containing the z-coord).
     *
     * @return the file name for the tile
     */
    String zPngName();

    /**
     * Sets the image file location.
     *
     * @param pngFile the image file
     */
    void setLocationOnDisk(File pngFile);

    /**
     * Reloads the tile image from disk.
     *
     * @return the tile image
     */
    BufferedImage loadImageFromDisk() throws IOException;

}
