/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.ImageUtils;

import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.meowster.mcquad.Region.NCHUNKS;
import static com.meowster.util.StringUtils.printOut;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Will generate a heatmap based on chunk modification times and the current
 * time. More recently modified chunks have brighter pixels.
 */
class ChunkHeatMap {

    // one color bucket per 24 hours...
    private static final long COLOR_BUCKET_SECONDS = 60 * 60 * 24;

    private static final java.awt.Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color[] BUCKETS = {
            // Blues defined by <H,S,L> with lightness percent shown
            new Color(0x052539), // 12%
            new Color(0x062e47), // 15%
            new Color(0x073755), // 18%
            new Color(0x084063), // 21%
            new Color(0x094971), // 24%
            new Color(0x0a537f), // 27%
            new Color(0x0b5c8e), // 30%
            new Color(0x0d659c), // 33%
            new Color(0x0e6eaa), // 36%
            new Color(0x0f77b8), // 39%
            new Color(0x1080c6), // 42%
            new Color(0x118ad4), // 45%
            new Color(0x1293e2), // 48%
            new Color(0x189bec), // 51%
            new Color(0x26a1ed), // 54%
            new Color(0x34a7ef), // 57%
            new Color(0x42adf0), // 60%
            new Color(0x50b3f1), // 63%
            new Color(0x5fb9f2), // 66%
            new Color(0x6dc0f3), // 69%
            new Color(0x7bc6f4), // 72%
            new Color(0x89ccf5), // 75%
            new Color(0x97d2f7), // 78%
            new Color(0xa5d8f8), // 81%
            new Color(0xb4def9), // 84%
            new Color(0xc2e4fa), // 87%
            new Color(0xd0ebfb), // 90%
            new Color(0xffffff), // 100%
    };

    private static final String CHUNKMAP = "chunkmap";
    private static final String PREFIX = "chunk";

    private final Map<Coord, TimestampArray> timestampMap = new HashMap<>();

    private int regionsPerSide;
    private int calX;
    private int calZ;
    private AuxFiles aux;

    private int pixels;
    private BufferedImage image;
    private Graphics2D g2;

    // seconds since epoch (January 1, 1970, 00:00:00 GMT)
    private long now = new Date().getTime() / 1_000;

    /**
     * Sets calibration parameters.
     *
     * @param regionsPerSide  number of regions per side of the quad map
     * @param quadCalibration offset of region origin into quad map
     * @return self, for chaining
     */
    ChunkHeatMap calibrate(int regionsPerSide, Coord quadCalibration) {
        this.regionsPerSide = regionsPerSide;
        Coord cal = quadCalibration.div2();
        calX = cal.x();
        calZ = cal.z();
        return this;
    }

    /**
     * Sets up the AuxFiles instance for the given auxiliary directory.
     *
     * @param auxDir auxiliary directory
     * @return self, for chaining
     */
    ChunkHeatMap auxdir(File auxDir) {
        aux = new AuxFiles(auxDir, CHUNKMAP, PREFIX);
        return this;
    }

    /**
     * Stores chunk access time data for the given region.
     *
     * @param r the region
     */
    void store(Region r) {
        timestampMap.put(r.coord(), new TimestampArray(r));
    }

    /**
     * Generates and writes the chunk heat map images.
     */
    void writeImages() {
        computeImage();
        ImageUtils.writeImageToDisk(image, aux.dated());
        ImageUtils.writeImageToDisk(image, aux.latest());
    }

    private void computeImage() {
        createBlankImage();
        printOut("Generating Chunk heatmap...[{}x{}]", pixels, pixels);

        for (TimestampArray array : timestampMap.values()) {
            int rx = array.coord.x();
            int rz = array.coord.z();
            int qx = rx - calX;
            int qz = rz - calZ;

            for (int cz = 0; cz < NCHUNKS; cz++) {
                for (int cx = 0; cx < NCHUNKS; cx++) {
                    int ts = array.timeStampAt(cx, cz);
                    g2.setColor(computeColor(ts));
                    g2.fillRect(qx * NCHUNKS + cx, qz * NCHUNKS + cz, 1, 1);
                }
            }
        }
    }

    private Color computeColor(int ts) {
        if (ts == 0) {
            return BACKGROUND_COLOR;
        }

        long since = now - ts;                       // seconds since mod
        long days = since / COLOR_BUCKET_SECONDS;    // days since mod
        int index = BUCKETS.length - (int) days - 1;

        return BUCKETS[index < 0 ? 0 : index];
    }

    private void createBlankImage() {
        pixels = regionsPerSide * NCHUNKS;
        image = new BufferedImage(pixels, pixels, TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setBackground(BACKGROUND_COLOR);
        g2.clearRect(0, 0, pixels, pixels);
    }


    /**
     * Timestamp integer array wrapper, encapsulating the timestamps of
     * chunks in a single region.
     */
    private static class TimestampArray {
        private final Coord coord;
        private final int[] array;

        TimestampArray(Region r) {
            coord = r.coord();
            array = r.extractChunkTimestamps();
        }

        /**
         * Returns the timestamp at relative coords (0..31)
         *
         * @param cx chunk x-coord
         * @param cz chunk z-coord
         * @return timestamp
         */
        private int timeStampAt(int cx, int cz) {
            return outOfBounds(cx, cz) ? 0 : array[cx + cz * NCHUNKS];
        }

        private boolean outOfBounds(int x, int z) {
            return x < 0 || x >= NCHUNKS || z < 0 || z >= NCHUNKS;
        }
    }
}
