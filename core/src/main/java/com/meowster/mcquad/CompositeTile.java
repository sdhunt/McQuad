/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Embodies a composite tile, made from four individual tiles in a 2x2
 * configuration.
 *
 * @author Simon Hunt
 */
public class CompositeTile {
    private static final int NPIXELS = 256; // pixel dimension of tile
    private static final int HALF = NPIXELS / 2;

    private BufferedImage image;

    /**
     * Constructs a composite tile from the given quadrant tiles.
     *
     * @param tl top-left quadrant
     * @param tr top-right quadrant
     * @param bl bottom-left quadrant
     * @param br bottom-right quadrant
     */
    public CompositeTile(QuadTile tl, QuadTile tr, QuadTile bl, QuadTile br) {
        image = new BufferedImage(NPIXELS, NPIXELS, TYPE_INT_ARGB);
        compose(tl, 0, 0);
        compose(tr, HALF, 0);
        compose(bl, 0, HALF);
        compose(br, HALF, HALF);
    }

    private void compose(QuadTile tile, int offx, int offz) {
        if (tile == null)
            return; // nothing to do

        // the quad-tile's image needs to be reloaded from disk...
        try {
            BufferedImage tileImage = tile.loadImageFromDisk();
            if (tileImage == null)
                return; // unable to load image

            for (int z = 0; z < NPIXELS; z += 2) {
                int z2 = z/2;
                for (int x = 0; x < NPIXELS; x += 2) {
                    int x2 = x/2;
                    Color av = getAvColor(tileImage, x, z);
                    image.setRGB(offx + x2, offz + z2, av.toInt());
                }
            }

        } catch (IOException e) {
            // TODO: should we try and write *something* in the tile quadrant?
            System.err.println("Failed to load image for: " + tile);
        }
    }

    private Color getAvColor(BufferedImage tileImage, int x, int z) {
        int a = tileImage.getRGB(x, z);
        int b = tileImage.getRGB(x+1, z);
        int c = tileImage.getRGB(x, z+1);
        int d = tileImage.getRGB(x+1, z+1);
        return Color.averageColor(a, b, c, d);
    }

    /**
     * Returns the generated image for this tile.
     *
     * @return the generated image
     */
    public BufferedImage getImage() {
        return image;
    }
}
