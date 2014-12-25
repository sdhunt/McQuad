/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
* Abstract base class of a quad tile implementation.
*
* @author Simon Hunt
*/
abstract class AbsQuadTile implements QuadTile {

    private static final String FILE_PREFIX = "t.";
    private static final String DOT = ".";
    private static final String FILE_SUFFIX = ".png";

    protected Coord coord;
    protected BufferedImage image;
    protected File onDisk;

    @Override
    public BufferedImage image() {
        return image;
    }

    @Override
    public Coord coord() {
        return coord;
    }

    @Override
    public void releaseResources() {
        image = null;
    }

    @Override
    public String pngName() {
        return FILE_PREFIX + coord.x() + DOT + coord.z() + FILE_SUFFIX;
    }

    @Override
    public void setLocationOnDisk(File pngFile) {
        onDisk = pngFile;
    }

    @Override
    public BufferedImage loadImageFromDisk() throws IOException {
        return onDisk == null ? null : ImageIO.read(onDisk);
    }
}
