/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     * @param tilesDir the top level tiles output directory
     * @param level    the source level
     */
    public ZoomQuadLevelBuilder(File tilesDir, QuadLevel level) {
        super(tilesDir);
        this.sourceLevel = level;
    }

    @Override
    public void prepare(Set<Coord> stale) {
        toProcess = stale;
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
                Coord c = new Coord(a / 2, b / 2);
                if (toProcess != null && !toProcess.contains(c)) {
                    continue;
                    // TODO: how to track tiles that were skipped?
                }

                // get TopLeft, TopRight, BottomLeft, BottomRight tiles
                QuadTile tl = gimmeATile(a, b);
                QuadTile tr = gimmeATile(a + 1, b);
                QuadTile bl = gimmeATile(a, b + 1);
                QuadTile br = gimmeATile(a + 1, b + 1);
                // generate new tile from the 4 input tiles (zoom out 1 level)
                tile = mergeTiles(a, b, tl, tr, bl, br);
                q.addTile(tile);

                if (tile != null) {
                    tiles.add(tile);
                }
            }

            if (!suppressWrite && !tiles.isEmpty()) {
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

    private QuadTile gimmeATile(int a, int b) {
        // first, check to see if the tile is in-memory...
        QuadTile qt = sourceLevel.at(a, b);

        if (toProcess != null && qt == null) {
            // hmmm, sparse rendering and tile not in memory; is it on disk?
            Coord coord = new Coord(a, b);
            String tp = AbsQuadTile.tilePath(sourceLevel.zoom(), coord);
            File tileFile = new File(tilesDir, tp);
            if (tileFile.exists()) {
                qt = new FromDiskQuadTile(coord, tileFile);
            }
        }
        return qt;
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
    void saveStats(List<LevelStats> stats) {
        stats.add(q.getStats());
    }
}
