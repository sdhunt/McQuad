/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * A base quad level builder. This implementation knows how
 * to create the base quad level (1x1 pixels per block), as well as the
 * two underlying zoomed-in levels (2x2 and 4x4 pixels per block).
 *
 * @author Simon Hunt
 */
public class BaseQuadLevelBuilder extends QuadLevelBuilder {

    private static final int NPIXELS = 256;
    private static final int HALF_NPIXELS = NPIXELS / 2;

    private final QuadData quadData;
    private final Coord regionToQuadDelta;

    private QdLvl levelZoomPlus0;
    private QdLvl levelZoomPlus1;
    private QdLvl levelZoomPlus2;

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
    public QuadLevel getLevel() {
        return levelZoomPlus0;
    }

    @Override
    void reportStats(List<LevelStats> stats) {
        // NOTE: add stats in order from deepest zoom upwards
        stats.add(levelZoomPlus2.getStats());
        stats.add(levelZoomPlus1.getStats());
        stats.add(levelZoomPlus0.getStats());
    }

    @Override
    public void prepare() {
        levelZoomPlus0 = makeQdLvl(0);
        levelZoomPlus1 = makeQdLvl(1);
        levelZoomPlus2 = makeQdLvl(2);
    }

    private QdLvl makeQdLvl(int zoomPlus) {
        int zoom = quadData.baseZoom() + zoomPlus;
        int factor = 1 << zoomPlus; // 1, 2, 4
        int blkPerTile = BLOCKS_PER_BASE_TILE / factor;
        Coord rqDelta = regionToQuadDelta.scale(factor);

        QdLvl q = new QdLvl();
        q.setZoom(zoom);
        q.setBlocksPerTileSide(blkPerTile);
        q.setOriginTile(rqDelta);
        q.setOriginDisplace(ORIGIN);   // TODO: review
        q.setOutputDir(new File(outputDir, q.name()));
        return q;
    }

    @Override
    public void createDirectory() {
        PathUtils.makeDir(outputDir, levelZoomPlus0.name());
        PathUtils.makeDir(outputDir, levelZoomPlus1.name());
        PathUtils.makeDir(outputDir, levelZoomPlus2.name());
    }

    @Override
    public void generateTiles() {
        genTiles(false);
    }

    /**
     * Generate tiles. The boolean is to facilitate simpler unit testing.
     *
     * @param suppressWrite if true, don't write image files to disk
     */
    public void genTiles(boolean suppressWrite) {
        /*
         NOTE:
         Since we will have the region data in memory, it makes sense
          to generate tiles for each of the three levels simultaneously.

           z +0             z +1            z +2
           1 x (256x256)    4 x (128x128)   16 x (64x64)
           +---+            +---+---+       +---+---+---+---+
           |   |            |   |   |       |   |   |   |   |
           +---+            +---+---+       +---+---+---+---+
                            |   |   |       |   |   |   |   |
                            +---+---+       +---+---+---+---+
                                            |   |   |   |   |
                                            +---+---+---+---+
                                            |   |   |   |   |
                                            +---+---+---+---+
         */

        levelZoomPlus0.startTracker();
        levelZoomPlus1.startTracker();
        levelZoomPlus2.startTracker();

        // don't forget, we are splitting the 512x512 block regions
        //  into four 256x256 block sub-regions, and then scaling each
        //  of those up an extra couple of zoom levels

        for (Region r : quadData.regionData().regions()) {
            generateZoomPlus0Tiles(r, suppressWrite);
            generateZoomPlus1Tiles(suppressWrite);
            generateZoomPlus2Tiles(suppressWrite);
        }

        levelZoomPlus2.stopTracker();
        levelZoomPlus1.stopTracker();
        levelZoomPlus0.stopTracker();
    }

    private void generateZoomPlus0Tiles(Region r, boolean suppressWrite) {
        RegionImageData ri = new RegionImageData(r, regionToQuadDelta);
        levelZoomPlus0.addTile(makeBaseQuadTile(ri, 0, 0));
        levelZoomPlus0.addTile(makeBaseQuadTile(ri, 1, 0));
        levelZoomPlus0.addTile(makeBaseQuadTile(ri, 0, 1));
        levelZoomPlus0.addTile(makeBaseQuadTile(ri, 1, 1));

        if (!suppressWrite) {
            writeTiles(levelZoomPlus0);
        }
    }

    private void generateZoomPlus1Tiles(boolean suppressWrite) {
        tileZoomIn(levelZoomPlus0, levelZoomPlus1, suppressWrite);
    }

    private void generateZoomPlus2Tiles(boolean suppressWrite) {
        tileZoomIn(levelZoomPlus1, levelZoomPlus2, suppressWrite);
    }

    private void tileZoomIn(QdLvl input, QdLvl output, boolean suppressWrite) {
        for (QuadTile tile : input.tiles()) {
            output.addTile(makeScaledTile(tile, 0, 0));
            output.addTile(makeScaledTile(tile, 1, 0));
            output.addTile(makeScaledTile(tile, 0, 1));
            output.addTile(makeScaledTile(tile, 1, 1));
        }

        if (!suppressWrite) {
            writeTiles(output);
        }
    }


    // return null if image is completely transparent (empty tile)
    private QuadTile makeBaseQuadTile(RegionImageData ri, int dx, int dz) {
        BufferedImage bi = ri.getImage(dx, dz);
        if (imageIsBlank(bi))
            return null;

        int x = ri.region().coord().x() * 2 + regionToQuadDelta.x() + dx;
        int z = ri.region().coord().z() * 2 + regionToQuadDelta.z() + dz;
        return new SubregionQTile(bi, x, z);
    }

    // scans the image: first non-transparent pixel will exit with false
    private boolean imageIsBlank(BufferedImage bi) {
        int a, b;
        int w = bi.getWidth();
        int h = bi.getHeight();
        for (a = 0; a < w; a++) {
            for (b = 0; b < h; b++) {
                if (bi.getRGB(a, b) != 0)
                    return false;
            }
        }
        return true;
    }

    private QuadTile makeScaledTile(QuadTile tile, int dx, int dz) {
        int ox = HALF_NPIXELS * dx;
        int oz = HALF_NPIXELS * dz;
        BufferedImage src = tile.image();
        int a, a2, b, b2, color;
        int aggr = 0;

        BufferedImage bi =
                new BufferedImage(NPIXELS, NPIXELS, TYPE_INT_ARGB);

        for (a = 0; a < HALF_NPIXELS; a++) {
            a2 = 2 * a;
            for (b = 0; b < HALF_NPIXELS; b++) {
                b2 = 2 * b;
                color = src.getRGB(ox + a, oz + b);
                aggr |= color; // aggregate color
                bi.setRGB(a2, b2, color);
                bi.setRGB(a2+1, b2, color);
                bi.setRGB(a2, b2+1, color);
                bi.setRGB(a2+1, b2+1, color);
            }
        }

        int x = tile.coord().x() * 2 + dx;
        int z = tile.coord().z() * 2 + dz;
        return aggr == 0 ? null : new SubregionQTile(bi, x, z);
    }

}
