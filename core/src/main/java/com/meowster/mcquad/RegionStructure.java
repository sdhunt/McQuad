/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Takes a Region and creates a data structure from which a tile
 * image may be generated.
 *
 * @author Simon Hunt
 */
public class RegionStructure {

    private static final int NPIXELS = 512; // pixel dimension of region
    private static final int TOTAL_PIXELS = NPIXELS * NPIXELS;

    private static final int NCHUNKS = 32;  // chunk dimension of region
    private static final int NBLOCKS = 16;  // block dimension of chunk

    private static final float SHADE_CLIP_MAX = 10.0f;
    private static final float SHADE_CLIP_MIN = -10.0f;
    private static final short SEA_LEVEL = 64;
    private static final float SEA_FACTOR = 7.0f;
    private static final float SHADE_FACTOR = 8.0f;


    private final Region region;

    private final Color[] surfaceColor = new Color[TOTAL_PIXELS];
    private final int[] colorAsInts = new int[TOTAL_PIXELS];
    private final short[] surfaceHeight = new short[TOTAL_PIXELS];

    /**
     * Creates a region structure from the given region.
     *
     * @param region the region for which a structure is to be created
     */
    public RegionStructure(Region region) {
        this.region = region;
        prerender();
        shade();
        covertColorsToInts();
    }

    private void covertColorsToInts() {
        // Note, 0 (default int value) corresponds to transparent .. handy!
        for (int i = 0; i < TOTAL_PIXELS; i++) {
            Color c = surfaceColor[i];
            if (c != null)
                colorAsInts[i] = c.toInt();
        }
    }

    private void shade() {
        int idx = -1;
        for (int z = 0; z < NPIXELS; z++) {
            for (int x = 0; x < NPIXELS; x++) {
                idx++;
                if (transparent(surfaceColor[idx]))
                    continue;

                float dyx = computeDyx(idx, x);
                float dyz = computeDyz(idx, z);
                int shadeAmount = computeShadeAmount(idx, dyx, dyz);
                surfaceColor[idx] = surfaceColor[idx].shade(shadeAmount);
            }
        }
    }

    private boolean transparent(Color color) {
        return color == null || color.alpha() == 0;
    }

    private float computeDyx(int idx, int x) {
        if (x == 0)
            return heightDiff(idx + 1, idx);
        else if (x == NPIXELS - 1)
            return heightDiff(idx, idx - 1);
        return heightDiff(idx + 1, idx - 1) * 2;
    }

    private float computeDyz(int idx, int z) {
        if (z == 0)
            return heightDiff(idx + NPIXELS, idx);
        else if (z == NPIXELS - 1)
            return heightDiff(idx, idx - NPIXELS);
        return heightDiff(idx + NPIXELS, idx - NPIXELS) * 2;
    }

    private float heightDiff(int a, int b) {
        return surfaceHeight[a] - surfaceHeight[b];
    }

    private int computeShadeAmount(int idx, float dyx, float dyz) {
        float shade = shadeClip(dyx + dyz) + wrtSeaLevel(idx);
        return (int) (shade * SHADE_FACTOR);
    }

    private float shadeClip(float v) {
        return v > SHADE_CLIP_MAX ? SHADE_CLIP_MAX :
                (v < SHADE_CLIP_MIN ? SHADE_CLIP_MIN : v);
    }

    private float wrtSeaLevel(int idx) {
        return (surfaceHeight[idx] - SEA_LEVEL) / SEA_FACTOR;
    }


    private void prerender() {
        // iterate over the chunks in the region...
        for (int cz = 0; cz < NCHUNKS; cz++) {
            for (int cx = 0; cx < NCHUNKS; cx++) {
                DataInputStream dis = region.getChunkDataStream(cx, cz);
                if (dis == null)
                    continue;

                Chunk chunk = new Chunk(dis);
                chunk.computeColorsAndHeights();
                Color[][] chunkSurface = chunk.getSurfaceColors();
                short[][] chunkHeight = chunk.getSurfaceHeights();
                storeData(cx, cz, chunkSurface, chunkHeight);
            }
        }
    }

    private void storeData(int cx, int cz, Color[][] chunkSurface,
                           short[][] chunkHeight) {
        for (int z = 0; z < NBLOCKS; z++) {
            int offset = NPIXELS * (NBLOCKS * cz + z) + NBLOCKS * cx;
            System.arraycopy(chunkSurface[z], 0, surfaceColor, offset, NBLOCKS);
            System.arraycopy(chunkHeight[z], 0, surfaceHeight, offset, NBLOCKS);
        }
    }

    /**
     * Returns a buffered image generated for the region.
     *
     * @return an image of the region
     */
    public BufferedImage getImage() {
        // render the buffered image from the computed surface color data
        BufferedImage bi =
                new BufferedImage(NPIXELS, NPIXELS, TYPE_INT_ARGB);
        for (int z = 0; z < NPIXELS; z++)
            bi.setRGB(0, z, NPIXELS, 1, colorAsInts, NPIXELS * z, NPIXELS);
        return bi;
    }
}
