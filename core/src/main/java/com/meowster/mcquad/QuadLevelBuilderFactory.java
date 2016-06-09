/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;

/**
 * A factory for {@link QuadLevelBuilder}s.
 *
 * @author Simon Hunt
 */
public class QuadLevelBuilderFactory {

    private final File tileDir;

    public QuadLevelBuilderFactory(File tilesDir) {
        this.tileDir = tilesDir;
    }

    /**
     * Creates a quad level builder that knows how to create the quad
     * levels for the 1x1, 2x2, and 4x4 pixels-per-block tiles.
     *
     * @param quadData the source data
     * @return a quad level builder
     */
    public QuadLevelBuilder createBuilder(QuadData quadData) {
        return new BaseQuadLevelBuilder(tileDir, quadData);
    }

    /**
     * Creates a quad level builder that knows how to create a quad
     * level zoomed out by 1 level from the specified level.
     *
     * @param level the source level (zoom factor Z)
     * @return a builder for the level (zoom factor Z-1)
     */
    public QuadLevelBuilder createBuilder(QuadLevel level) {
        return new ZoomQuadLevelBuilder(tileDir, level);
    }
}
