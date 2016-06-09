/*
 * Copyright (c) 2016 Meowster.com
 */
package com.meowster.mcquad;

import java.io.File;

/**
 * Quad tile implementation that sources its data from a .png file.
 */
public class FromDiskQuadTile extends AbsQuadTile {

    /**
     * Instantiates a quad tile from a .png file.
     *
     * @param tile the tile location on disk
     */
    public FromDiskQuadTile(Coord coord, File tile) {
        this.coord = coord;
        this.onDisk = tile;
    }

    @Override
    public String toString() {
        return "FromDiskQTile{" +
                "coord=" + coord +
                "file=" + onDisk +
                "}";
    }
}
