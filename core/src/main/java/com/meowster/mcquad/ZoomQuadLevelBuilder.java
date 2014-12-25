/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;

/**
 * A zoom quad level builder. This implementation knows how to create a quad
 * level that is zoomed-out by one from the supplied source level.
 *
 * @author Simon Hunt
 */
public class ZoomQuadLevelBuilder extends QuadLevelBuilder {

    private final QuadLevel sourceLevel;

    /**
     * Constructs a zoom quad level builder.
     *
     * @param outputDir the top level output directory
     * @param level the source level
     */
    public ZoomQuadLevelBuilder(File outputDir, QuadLevel level) {
        super(outputDir);
        this.sourceLevel = level;
    }

    @Override
    public void prepare() {
        // TODO ...
    }

    @Override
    public void createDirectory() {
        // TODO ...
    }

    @Override
    public void generateTiles() {
        // TODO ...
    }

    @Override
    public QuadLevel getLevel() {
        return null;  // TODO ...
    }
}
