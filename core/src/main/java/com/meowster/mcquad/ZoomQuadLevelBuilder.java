/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.print;
import static com.meowster.util.StringUtils.printOut;

/**
 * A zoom quad level builder. This implementation knows how to create a quad
 * level that is zoomed-out by one from the supplied source level.
 *
 * @author Simon Hunt
 */
public class ZoomQuadLevelBuilder extends QuadLevelBuilder {

    private final QuadLevel sourceLevel;
    private QdLvl q;

    /**
     * Constructs a zoom quad level builder.
     *
     * @param outputDir the top level output directory
     * @param level     the source level
     */
    public ZoomQuadLevelBuilder(File outputDir, QuadLevel level) {
        super(outputDir);
        this.sourceLevel = level;
    }

    @Override
    public void prepare() {
        q = new QdLvl();
        q.setZoom(sourceLevel.zoom() - 1);
        q.setBlocksPerTileSide(sourceLevel.blocksPerTileSide() * 2);
        q.setOriginTile(sourceLevel.originTile().div2());
        q.setOriginDisplace(ORIGIN);    // TODO: review
        q.setOutputDir(new File(tilesDir, q.name()));
    }

    @Override
    public void createDirectory() {
        PathUtils.createIfNeedBe(tilesDir, q.name());
    }

    @Override
    public void generateTiles() {
        genTiles(false);
    }

    private void genTiles(boolean suppressWrite) {
        final int dim = sourceLevel.dim();
        List<QuadTile> tiles = new ArrayList<>();
        QuadTile tile;

        q.startTracker();

        printOut(String.format(EOL + "Rendering zoom level %d:", q.zoom()));
        for (int a = 0; a < dim; a += 2) {
            for (int b = 0; b < dim; b += 2) {
                // get TopLeft, TopRight, BottomLeft, BottomRight tiles
                QuadTile tl = sourceLevel.at(a, b);
                QuadTile tr = sourceLevel.at(a + 1, b);
                QuadTile bl = sourceLevel.at(a, b + 1);
                QuadTile br = sourceLevel.at(a + 1, b + 1);
                // generate new tile from the 4 input tiles (zoom out 1 level)
                tile = mergeTiles(a, b, tl, tr, bl, br);
                q.addTile(tile);

                if (tile != null) {
                    tiles.add(tile);
                }
            }

            if (!suppressWrite) {
                writeTiles(tiles, q.outputDir());
            }

            printMark(a + 2);

            releaseTiles(tiles);
            tiles.clear();
        }

        print(EOL);

        q.stopTracker();
        printOut(q.getStats().toString());
    }

    // Merge the four tiles into a single tile "one-fourth the size"
    private QuadTile mergeTiles(int a, int b, QuadTile tl, QuadTile tr,
                                QuadTile bl, QuadTile br) {
        // if no input tiles, no output tile
        return (tl == null && tr == null && bl == null && br == null)
                ? null : new FourQTile(a, b, tl, tr, bl, br);
    }

    @Override
    public QuadLevel getLevel() {
        return q;
    }

    @Override
    void reportStats(List<LevelStats> stats) {
        stats.add(q.getStats());
    }
}
