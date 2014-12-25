/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;

/**
 * A base quad level builder. This implementation knows how
 * to create the base quad level (1x1 pixels per block), as well as the
 * two underlying zoomed-in levels (2x2 and 4x4 pixels per block).
 *
 * @author Simon Hunt
 */
public class BaseQuadLevelBuilder extends QuadLevelBuilder {

    private final QuadData quadData;
    private final Coord regionToQuadDelta;

    private QdLvl levelZoomPlus1;
    private QdLvl levelZoomPlus2;
    private QdLvl levelZoomPlus3;

    /**
     * Constructs a base quad level builder.
     *
     * @param outputDir the top level output directory
     * @param quadData the source data
     */
    public BaseQuadLevelBuilder(File outputDir, QuadData quadData) {
        super(outputDir);
        this.quadData = quadData;
        regionToQuadDelta = quadData.calibration().negation();
    }

    @Override
    public void prepare() {
        levelZoomPlus1 = new QdLvl();
        levelZoomPlus1.setZoom(quadData.maxZoom() + 1);
        levelZoomPlus1.setBlocksPerTile(BLOCKS_PER_REGION/2);
        levelZoomPlus1.setOriginTile(regionToQuadDelta); // FIXME
        levelZoomPlus1.setOriginDisplace(ORIGIN);        // FIXME

        // TODO: level +2 and level +3
    }

    @Override
    public void createDirectory() {
        PathUtils.makeDir(outputDir, levelZoomPlus1.name());
        // TODO add levels +2, +3
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
