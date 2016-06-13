/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.TextFile;

import java.io.File;

/**
 * Encapsulates the JavaScript parameterization.
 */
class ParameterizedJs {

    private static final int HEAT_MAP_DIV_MIN_SIZE = 400;

    /* from base zoom to max zoom (2), plus also polymap zoom (2) */
    private static final int ZOOM_PLUS = 2;

    private static final String JS_FILENAME = "map-config.js";

    private static final String OBJECT_HEAD = "McQuad = {%n  cfg: {";
    private static final String OBJECT_TAIL = "    dummy: 0%n  }%n};";

    private static final String VERSION = "    version: '%s',";
    private static final String BASE_ZOOM = "    baseZoom: %d,";
    private static final String ZOOM_LIMIT = "    zoomLimit: %d.49,";
    private static final String HEAT_MAP_SIZE = "    heatMap: %d,";
    private static final String HEAT_MAP_DIV = "    heatMapDiv: %d,";
    private static final String REGION_PIXEL = "    regionPixel: %d,";


    private TextFile tf;

    /**
     * Creates a parameterized JavaScript object instance, which will be
     * written into the specified directory.
     *
     * @param wwwDir top level web directory
     */
    ParameterizedJs(File wwwDir) {
        tf = new TextFile(new File(wwwDir, JS_FILENAME))
            .add(String.format(OBJECT_HEAD));
    }

    /**
     * Sets the version string.
     *
     * @param version version string
     * @return self, for chaining
     */
    ParameterizedJs version(String version) {
        tf.add(String.format(VERSION, version));
        return this;
    }

    /**
     * Sets the max zoom factor.
     *
     * @param baseZoom computed max zoom factor
     * @return self, for chaining
     */
    ParameterizedJs baseZoom(int baseZoom) {
        tf.add(String.format(BASE_ZOOM, baseZoom))
                .add(String.format(ZOOM_LIMIT, baseZoom + ZOOM_PLUS));
        return this;
    }


    /**
     * Sets the heat map size in pixels.
     *
     * @param pixels heat map size
     * @return self, for chaining
     */
    ParameterizedJs heatMapSizePx(int pixels) {
        tf.add(String.format(HEAT_MAP_SIZE, pixels));

        int divSize = Math.max(HEAT_MAP_DIV_MIN_SIZE, pixels);
        tf.add(String.format(HEAT_MAP_DIV, divSize));
        return this;
    }

    /**
     * Sets the size of a "region pixel" in the heat map.
     *
     * @param regionPixel region pixel size
     * @return self, for chaining
     */
    ParameterizedJs regionPixel(int regionPixel) {
        tf.add(String.format(REGION_PIXEL, regionPixel));
        return this;
    }

    /**
     * Completes the object instance and writes the file to disk.
     */
    void write() {
        tf.add(String.format(OBJECT_TAIL))
                .write();
    }
}
