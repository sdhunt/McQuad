/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;

/**
 * A quad tile composed of a region quadrant.
 *
 * @author Simon Hunt
 */
class SubregionQTile extends AbsQuadTile {

    SubregionQTile(BufferedImage image, int x, int z) {
        coord = new Coord(x, z);
        this.image = image;
    }

    @Override
    public void releaseResources() {
        super.releaseResources();
    }

    @Override
    public String toString() {
        return "SubregionQTile{" +
                "coord=" + coord +
                '}';
    }
}
