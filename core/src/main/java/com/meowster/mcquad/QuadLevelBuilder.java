/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.ImageUtils;
import com.meowster.util.PathUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.print;

/**
 * The partially implemented superclass for quad level builders.
 *
 * @author Simon Hunt
 */
abstract class QuadLevelBuilder {

    private static final int MAJOR_TICK = 50;
    private static final int MINOR_TICK = 10;
    private static final String MARK = ".";
    private static final String MAJOR_MARK = "#" + EOL;
    private static final String MINOR_MARK = "o";

    static final int BLOCKS_PER_BASE_TILE = 256;
    static final Coord ORIGIN = new Coord(0, 0);

    final File tilesDir;

    Set<Coord> toProcess;

    /**
     * Constructs the builder.
     *
     * @param tilesDir the top level tiles output directory
     */
    QuadLevelBuilder(File tilesDir) {
        this.tilesDir = tilesDir;
    }

    /**
     * This allows the builder to initialize its internal structures,
     * ready for operation. The list of coordinates are those quad tiles
     * that need to be rendered. If stale is null, it means we have to
     * re-render all the tiles, because the region-to-quad calibration
     * changed since last time.
     *
     * @param stale list of stale tile coordinates
     */
    abstract void prepare(Set<Coord> stale);

    /**
     * This instructs the builder to create the directory in which the
     * image tiles will be placed.
     */
    abstract void createDirectory();

    /**
     * This instructs the builder to create the image tiles, placing them
     * in the appropriate directory.
     *
     * @return the total number of tiles generated
     */
    abstract long generateTiles();

    /**
     * Returns the quad level that the builder built; to be used for creating
     * the next level up (zooming out).
     *
     * @return the quad level
     */
    abstract QuadLevel getLevel();

    /**
     * Builder should append related level stats to the given list.
     *
     * @param stats the list to which stats should be added
     */
    abstract void saveStats(List<LevelStats> stats);


    /**
     * Writes the image files to disk.
     *
     * @param tiles the tiles to write to disk
     */
    void writeTiles(List<QuadTile> tiles, File odir) {
        for (QuadTile tile : tiles) {
            File xDir = new File(odir, tile.xDirName());
            PathUtils.createIfNeedBe(xDir);

            File pngFile = new File(xDir, tile.zPngName());
            // remember where we stored the image on disk (for reloading)..
            tile.setLocationOnDisk(pngFile);
            ImageUtils.writeImageToDisk(tile.image(), pngFile);
        }
    }

    void releaseTiles(List<QuadTile> tiles) {
        for (QuadTile tile : tiles) {
            tile.releaseResources();
        }
        tiles.clear();
    }

    void printMark(int i) {
        print((i % MAJOR_TICK == 0) ? MAJOR_MARK :
                (i % MINOR_TICK == 0) ? MINOR_MARK : MARK);
    }

    /**
     * Returns the set of stale coordinates for one level zoomed out.
     *
     * @return stale coordinates one level up
     */
    Set<Coord> zoomedOutStale() {
        if (toProcess == null) {
            return null;
        }
        Set<Coord> result = new HashSet<>();
        for (Coord c : toProcess) {
            result.add(c.div2());
        }
        return result;
    }
}
