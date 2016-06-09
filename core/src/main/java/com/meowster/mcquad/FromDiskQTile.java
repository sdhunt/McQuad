/*
 * Copyright (c) 2014-2016 Meowster.com
 */
package com.meowster.mcquad;

import java.io.File;

/**
 * Quad tile implementation that sources its data from a .png file.
 */
class FromDiskQTile extends AbsQuadTile {

    /**
     * Instantiates a quad tile from a .png file.
     *
     * @param tile the tile location on disk
     */
    FromDiskQTile(Coord coord, File tile) {
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
