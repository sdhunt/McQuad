/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.ImageUtils;

import java.io.File;

/**
 * Builds {@link com.meowster.mcquad.QuadLevel}s.
 *
 * @author Simon Hunt
 */
class QuadLevelBuilderOld {

    private static final String E_ZOOM_2_OR_MORE = "Zoom level must be 2 or more";
    private static final int BLOCKS_PER_REGION = 512;

    private final QuadData quadData;
    private final QuadLevel sourceLevel;
    private final boolean zoomIn;

    private Coord regionToQuadDelta;
    private QdLvl generatedLevel;

    /**
     * Constructs a builder for the given quad data.
     *
     * @param quadData the quad data
     */
    QuadLevelBuilderOld(QuadData quadData) {
        this.quadData = quadData;
        regionToQuadDelta = quadData.calibration().negation();
        sourceLevel = null;
        zoomIn = false;
    }

    /**
     * Constructs a builder from the given source level. That is, the builder
     * will build a level with zoom factor one less than the source level,
     * where we will "reduce" each group of 2x2 pixels to a single pixel.
     *
     * @param level the level to build from
     * @throws IllegalArgumentException if zoom level of specified level
     *          is less than 2
     */
    QuadLevelBuilderOld(QuadLevel level) {
        this(level, false);
    }

    /**
     * Constructs a builder from the given source level. If zoomIn is true, we
     * will be building a level with zoom factor one more than the source
     * level, where we will "enlarge" each pixel by a factor of 2 (1x1 => 2x2).
     * If zoomIn is false, we will be building a level with a zoom factor one
     * less than the source level, where we will "reduce" each group of
     * 2x2 pixels to a single pixel.
     *
     * @param level the level to build from
     * @param zoomIn true to zoom in to the given level, false to zoom out
     * @throws IllegalArgumentException if zoom level of specified level
     *          is less than 2
     */

    public QuadLevelBuilderOld(QuadLevel level, boolean zoomIn) {
        if (level.zoom() < 2)
            throw new IllegalArgumentException(E_ZOOM_2_OR_MORE);

        quadData = null;
        sourceLevel = level;
        this.zoomIn = zoomIn;
    }

    /**
     * Prepares the quad level for use.
     *
     * @return the generated quad level
     */
    QuadLevel prepare() {
        generatedLevel = quadData != null ? prepareFromQuadData()
                : prepareFromSourceLevel();
        return generatedLevel;
    }

    /**
     * Generates the image tiles and writes them to disk.
     */
    public void genTiles() {
        genTiles(false);
    }

    /**
     * Generates the image tiles and writes them to disk, unless suppressed.
     *
     * @param suppressWriteToDisk if true, don't write any data to disk
     */
    public void genTiles(boolean suppressWriteToDisk) {
        // NOTE: We want to free up resources between each tile so we aren't
        //       trying to hold ALL the tile image data in memory at once.
        if (quadData != null)
            generateFromQuadData(suppressWriteToDisk);
        else
            generateFromSourceLevel(suppressWriteToDisk);
    }


    private QdLvl prepareFromQuadData() {
        QdLvl ql = new QdLvl();
        ql.setZoom(quadData.maxZoom());
        ql.setBlocksPerTile(BLOCKS_PER_REGION);
        ql.setOriginTile(regionToQuadDelta);
        ql.setOriginDisplace(new Coord(0, 0));
        return ql;
    }

    private void generateFromQuadData(boolean suppressWriteToDisk) {
        final QdLvl ql = generatedLevel;
        ql.startTracker();
        for (Region r : quadData.regionData().regions()) {
            RegionQTile qtile = new RegionQTile(r, regionToQuadDelta);
            if (!suppressWriteToDisk && !r.mock()) // TODO: can we lose "mock"?
                writeTileImageToDisk(ql, qtile);
            qtile.releaseResources();
            ql.addTile(qtile);
        }
        ql.stopTracker();
    }

    private void writeTileImageToDisk(QuadLevel ql, QuadTile qtile) {
        File pngFile = new File(ql.outputDir(), qtile.pngName());
        qtile.setLocationOnDisk(pngFile);
        ImageUtils.writeImageToDisk(qtile.image(), pngFile);
    }


    private QdLvl prepareFromSourceLevel() {
        QdLvl ql = new QdLvl();
        computeAttributes(ql, sourceLevel);
        return ql;
    }

    private void computeAttributes(QdLvl ql, QuadLevel sourceLevel) {
        final int blocksPerTile = sourceLevel.blocksPerTile();
        ql.setZoom(sourceLevel.zoom() - 1);
        ql.setBlocksPerTile(blocksPerTile * 2);
        Coord sourceOrig = sourceLevel.originTile();
        Coord sourceDisp = sourceLevel.originDisplace();
        int sox = sourceOrig.x();
        int soz = sourceOrig.z();
        int xDisp = sourceDisp.x() + ((sox % 2) * blocksPerTile);
        int zDisp = sourceDisp.z() + ((soz % 2) * blocksPerTile);
        ql.setOriginTile(new Coord(sox / 2, soz / 2));
        ql.setOriginDisplace(new Coord(xDisp, zDisp));
    }

    // Merge the four tiles into a single tile "one-fourth the size"
    private QuadTile mergeTiles(int a, int b, QuadTile tl, QuadTile tr,
                                QuadTile bl, QuadTile br) {
        // if no input tiles, no output tile
        return (tl == null && tr == null && bl == null && br == null)
                ? null : new FourQTile(a, b, tl, tr, bl, br);
    }


    private void generateFromSourceLevel(boolean suppressWriteToDisk) {
        final QdLvl ql = generatedLevel;
        final int dim = sourceLevel.dim();
        ql.startTracker();
        for (int a=0; a<dim; a+=2) {
            for (int b=0; b<dim; b+=2) {
                // get TopLeft, TopRight, BottomLeft, BottomRight tiles
                QuadTile tl = sourceLevel.at(a, b);
                QuadTile tr = sourceLevel.at(a+1, b);
                QuadTile bl = sourceLevel.at(a, b+1);
                QuadTile br = sourceLevel.at(a+1, b+1);
                // generate new tile from the 4 input tiles (zoom out 1 level)
                QuadTile merged = mergeTiles(a, b, tl, tr, bl, br);
                if (merged != null) {
                    ql.addTile(merged);
                    if (!suppressWriteToDisk)
                        writeTileImageToDisk(ql, merged);
                    merged.releaseResources();
                }
            }
        }
        ql.stopTracker();
    }

}
