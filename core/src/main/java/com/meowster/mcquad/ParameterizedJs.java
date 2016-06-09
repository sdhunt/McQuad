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

    private static final String JS_FILENAME = "map-config.js";

    private static final String OBJECT_HEAD = "McQuad = {";
    private static final String OBJECT_TAIL = "};";

    private static final String MAX_ZOOM = "  maxZoom: %d";

    /**
     * Creates the parameterized JavaScript file.
     *
     * @param wwwDir  top level web directory
     * @param maxZoom computed max zoom level
     */
    ParameterizedJs(File wwwDir, int maxZoom) {
        TextFile tf = new TextFile(new File(wwwDir, JS_FILENAME));
        tf.add(OBJECT_HEAD)
                .add(String.format(MAX_ZOOM, maxZoom))
                .add(OBJECT_TAIL)
                .write();
    }
}
