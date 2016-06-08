/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.print;
import static com.meowster.util.StringUtils.printOut;
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
     * @param tilesDir the top level tiles output directory
     * @param quadData the source data
     */
    public BaseQuadLevelBuilder(File tilesDir, QuadData quadData) {
        super(tilesDir);
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
        q.setOutputDir(new File(tilesDir, q.name()));
        return q;
    }

    @Override
    public void createDirectory() {
        PathUtils.createIfNeedBe(tilesDir, levelZoomPlus0.name());
        PathUtils.createIfNeedBe(tilesDir, levelZoomPlus1.name());
        PathUtils.createIfNeedBe(tilesDir, levelZoomPlus2.name());
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
        List<QuadTile> tiles0, tiles1, tiles2;

        printOut(String.format(EOL + "Rendering zoom levels %d, %d, and %d:",
                levelZoomPlus0.zoom(),
                levelZoomPlus1.zoom(),
                levelZoomPlus2.zoom()));

        int regionCount = 0;
        for (Region r : quadData.regionData().regions()) {
            regionCount++;

            tiles0 = generateZoomPlus0Tiles(r, suppressWrite);
            tiles1 = tileZoomIn(tiles0, levelZoomPlus1, suppressWrite);
            tiles2 = tileZoomIn(tiles1, levelZoomPlus2, suppressWrite);

            printMark(regionCount);

            releaseTiles(tiles0);
            releaseTiles(tiles1);
            releaseTiles(tiles2);
        }

        print(EOL);

        levelZoomPlus2.stopTracker();
        levelZoomPlus1.stopTracker();
        levelZoomPlus0.stopTracker();

        printOut(levelZoomPlus2.getStats().toString());
        printOut(levelZoomPlus1.getStats().toString());
        printOut(levelZoomPlus0.getStats().toString());
    }

    private List<QuadTile> generateZoomPlus0Tiles(Region r, boolean suppressWrite) {
        RegionImageData ri = new RegionImageData(r, regionToQuadDelta);
        List<QuadTile> tiles = new ArrayList<>();
        QuadTile tile;

        tile = makeBaseQuadTile(ri, 0, 0);
        levelZoomPlus0.addTile(tile);
        if (tile != null)
            tiles.add(tile);

        tile = makeBaseQuadTile(ri, 1, 0);
        levelZoomPlus0.addTile(tile);
        if (tile != null)
            tiles.add(tile);

        tile = makeBaseQuadTile(ri, 0, 1);
        levelZoomPlus0.addTile(tile);
        if (tile != null)
            tiles.add(tile);

        tile = makeBaseQuadTile(ri, 1, 1);
        levelZoomPlus0.addTile(tile);
        if (tile != null)
            tiles.add(tile);

        if (!suppressWrite) {
            writeTiles(tiles, levelZoomPlus0.outputDir());
        }

        return tiles;
    }

    private List<QuadTile> tileZoomIn(List<QuadTile> inTiles, QdLvl destLvl,
                                      boolean suppressWrite) {
        List<QuadTile> tiles = new ArrayList<>();
        QuadTile tile;

        for (QuadTile inTile : inTiles) {
            tile = makeScaledTile(inTile, 0, 0);
            destLvl.addTile(tile);
            if (tile != null)
                tiles.add(tile);

            tile = makeScaledTile(inTile, 1, 0);
            destLvl.addTile(tile);
            if (tile != null)
                tiles.add(tile);

            tile = makeScaledTile(inTile, 0, 1);
            destLvl.addTile(tile);
            if (tile != null)
                tiles.add(tile);

            tile = makeScaledTile(inTile, 1, 1);
            destLvl.addTile(tile);
            if (tile != null)
                tiles.add(tile);

        }

        if (!suppressWrite) {
            writeTiles(tiles, destLvl.outputDir());
        }

        return tiles;
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
                bi.setRGB(a2 + 1, b2, color);
                bi.setRGB(a2, b2 + 1, color);
                bi.setRGB(a2 + 1, b2 + 1, color);
            }
        }

        int x = tile.coord().x() * 2 + dx;
        int z = tile.coord().z() * 2 + dz;
        return aggr == 0 ? null : new SubregionQTile(bi, x, z);
    }

}
