/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
* A concrete implementation of {@link QuadLevel}.
*
* @author Simon Hunt
*/
class QdLvl implements QuadLevel {
    private final Map<Coord, QuadTile> tileMap = new HashMap<>();
    private int zoom;
    private int dim;
    private int blocksPerTileSide;
    private Coord originTile;
    private Coord originDisplace;
    private LevelStats stats = new LevelStats();
    private File outputDir;

    void addTile(QuadTile tile) {
        if (tile != null) {
            tileMap.put(tile.coord(), tile);
            stats.addStats(tile);
        } else {
            stats.incBlanks();
        }
    }

    void setZoom(int zoom) {
        this.zoom = zoom;
        stats.setZoom(zoom);
        dim = 1;
        for (int i=0; i<zoom; i++)
            dim *=2;
    }

    @Override
    public int zoom() {
        return zoom;
    }

    @Override
    public int dim() {
        return dim;
    }

    @Override
    public Collection<QuadTile> tiles() {
        return Collections.unmodifiableCollection(tileMap.values());
    }

    @Override
    public QuadTile at(int a, int b) {
        return tileMap.get(new Coord(a, b));
    }

    @Override
    public String schematic() {
        StringBuilder sb = new StringBuilder();
        for (int b=0; b<dim; b++) {
            for (int a=0; a<dim; a++) {
                sb.append(tileChar(at(a, b)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String tileChar(QuadTile tile) {
        return tile == null ? " ."
                : tile.coord().equals(originTile) ? " O" : " #";
    }

    @Override
    public int blocksPerTileSide() {
        return blocksPerTileSide;
    }

    void setBlocksPerTileSide(int blocksPerTileSide) {
        this.blocksPerTileSide = blocksPerTileSide;
    }

    @Override
    public Coord originTile() {
        return originTile;
    }

    void setOriginTile(Coord originTile) {
        this.originTile = originTile;
    }

    @Override
    public Coord originDisplace() {
        return originDisplace;
    }

    @Override
    public LevelStats getStats() {
        return stats;
    }

    @Override
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public File outputDir() {
        return outputDir;
    }

    void setOriginDisplace(Coord originDisplace) {
        this.originDisplace = originDisplace;
    }


    @Override
    public String name() {
        return "z" + zoom;
    }

    @Override
    public String toString() {
        return "QdLvl{" +
                "zoom=" + zoom +
                ", dim=" + dim +
                ", #tiles=" + tileMap.size() +
                ", blocksPerTileSide=" + blocksPerTileSide +
                ", originTile=" + originTile +
                ", originDisplace=" + originDisplace +
                '}';
    }

    /**
     * Starts the throughput tracker.
     */
    void startTracker() {
        stats.startTracker();
    }

    /**
     * Stops the throughput tracker.
     */
    void stopTracker() {
        stats.stopTracker();
    }
}
