/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;

/**
 * A data structure encapsulating a region such that 4 image tiles may be
 * produced from it.
 *
 * @author Simon Hunt
 */
class RegionImageData {
    private static final int NPIXELS = 256;

    private final Region region;
    private final Coord coord;
    private BufferedImage image;

    /**
     * Construct region image data from the given region.
     *
     * @param region            the source region
     * @param regionToQuadDelta offset from region coords to quad coords
     */
    RegionImageData(Region region, Coord regionToQuadDelta) {
        this.region = region;
        int x = region.coord().x() * 2 + regionToQuadDelta.x();
        int z = region.coord().z() * 2 + regionToQuadDelta.z();
        coord = new Coord(x, z);
        image = TileUtils.generateImage(region);
    }

    public Coord coord() {
        return coord;
    }

    public Region region() {
        return region;
    }

    /**
     * Returns a quadrant of the region image, given parameters dx and dz:
     * [0,0] upper left; [1,0] upper right;
     * [0,1] lower left; [1,1] lower right.
     *
     * @param dx left or right quadrant {0|1}
     * @param dz upper or lower quadrant {0|1}
     * @return the buffered image for the tile
     */
    BufferedImage getImage(int dx, int dz) {
        return image.getSubimage(dx * NPIXELS, dz * NPIXELS, NPIXELS, NPIXELS);
    }

    /**
     * Release the backing resources so that they may be garbage collected.
     */
    void releaseResources() {
        region.releaseResources();
        image = null;
    }

    @Override
    public String toString() {
        return "SubregionQTile{" +
                "region=" + region +
                ", coord=" + coord +
                '}';
    }
}
