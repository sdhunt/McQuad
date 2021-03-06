/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.ImageUtils;

import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.printOut;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Generates a heatmap image and writes it to the aux/heatmap directory.
 */
class HeatMapGenerator {
    private static final String HEATMAP = "heatmap";
    private static final String PREFIX = "heat";

    static final int PIXELS_PER_REGION = 8;

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color[] LEVELS = {
            new Color(0x402e16),
            new Color(0x4d381b),
            new Color(0x59411f),
            new Color(0x664a24),
            new Color(0x735428),
            new Color(0x805d2d),
            new Color(0x8c6631),
            new Color(0x997036),
            new Color(0xa6793a),
            new Color(0xb3823e),
            new Color(0xbf8843),
            new Color(0xcc9547),
            new Color(0xd99e4c),
            new Color(0xe6a750),
            new Color(0xf2b155),
            new Color(0xffd0a0),
    };
    private static final int MAX_TTL = 15;

    private BufferedImage image;
    private Graphics2D g2;
    private int pixels;
    private Coord offset;

    /**
     * Instantiates and executes heatmap generation.
     *
     * @param regionDim   number of regions per side of map
     * @param calibration region to quad calibration
     * @param ttlMap      region TTL data
     * @param auxDir      top level auxiliary output directory
     */
    HeatMapGenerator(int regionDim, Coord calibration,
                     Map<Coord, Integer> ttlMap, File auxDir) {
        offset = calibration.div2();

        printOut(EOL + "Generating REGION heatmap...");
        createEmptyMap(regionDim);
        for (Map.Entry<Coord, Integer> entry : ttlMap.entrySet()) {
            renderCell(adjust(entry.getKey()), entry.getValue());
        }

        AuxFiles aux = new AuxFiles(auxDir, HEATMAP, PREFIX);

        ImageUtils.writeImageToDisk(image, aux.dated());
        ImageUtils.writeImageToDisk(image, aux.latest());
    }

    private Coord adjust(Coord c) {
        return new Coord(c.x() - offset.x(), c.z() - offset.z());
    }

    private void renderCell(Coord coord, int ttl) {
        g2.setColor(computeColor(ttl));
        g2.fillRect(coord.x() * PIXELS_PER_REGION,
                coord.z() * PIXELS_PER_REGION,
                PIXELS_PER_REGION, PIXELS_PER_REGION);
    }

    private Color computeColor(int ttl) {
        int i = ttl > MAX_TTL ? MAX_TTL :
                ttl < 0 ? 0 : ttl;
        return LEVELS[i];
    }

    private void createEmptyMap(int regionsPerSide) {
        pixels = regionsPerSide * PIXELS_PER_REGION;
        image = new BufferedImage(pixels, pixels, TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setBackground(BACKGROUND_COLOR);
        g2.clearRect(0, 0, pixels, pixels);
    }

    /**
     * Returns the size of the heatmap image in pixels. Since the map is
     * square we know it will be NxN pixels.
     *
     * @return number of pixels to a side
     */
    int pixels() {
        return pixels;
    }
}
