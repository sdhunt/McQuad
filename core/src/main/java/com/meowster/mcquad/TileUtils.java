/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;

/**
 * Image utilities for creating the tile images from region data.
 * Implementation based on Togo's RegionRenderer.
 *
 * @author Simon Hunt
 */
public class TileUtils {


    /**
     * Creates an image of the specified region.
     *
     * @param region the region
     * @return an image of the region
     */
    public static BufferedImage generateImage(Region region) {
        return new RegionStructure(region).getImage();
    }

    /**
     * Creates a composite image from the specified quad tiles.
     *
     * @param tl top-left tile
     * @param tr top-right tile
     * @param bl bottom-left tile
     * @param br bottom-right tile
     * @return the composite image
     */
    public static BufferedImage generateImage(QuadTile tl, QuadTile tr,
                                              QuadTile bl, QuadTile br) {
        return new CompositeTile(tl, tr, bl, br).getImage();
    }
}
