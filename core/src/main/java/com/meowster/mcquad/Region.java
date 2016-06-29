/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.io.DataInputStream;
import java.io.File;

/**
 * Encapsulates data about a single region. Note that equals and hashCode
 * are written in terms of the coordinates only.
 *
 * @author Simon Hunt
 */
class Region {
    private static final String T_DOT = "t.";
    private static final String DOT = ".";
    private static final String DOT_PNG = ".png";

    private final Coord coord;
    private final File f;
    private final boolean mock;

    private RegionFile rf;

    /**
     * Constructs a region with the given coordinates, and the specified
     * region file. If mock is true the file is assumed to be a mock (empty).
     *
     * @param x    the x-coord
     * @param z    the z-coord
     * @param f    the region file
     * @param mock file is a mock
     */
    Region(int x, int z, File f, boolean mock) {
        this.coord = new Coord(x, z);
        this.f = f;
        this.mock = mock;
        rf = f == null ? null : new RegionFile(f, mock);
    }

    /**
     * Constructor for unit testing, where just coordinates are specified;
     * the file is set to null.
     *
     * @param x the x-coord
     * @param z the z-coord
     */
    Region(int x, int z) {
        this(x, z, null, true);
    }

    /**
     * Constructor for creating region file (not a mock).
     *
     * @param x    the x-coord
     * @param z    the z-coord
     * @param f    the region file
     */
    Region(int x, int z, File f) {
        this(x, z, f, false);
    }

    @Override
    public String toString() {
        return "Region{" + coord + ", mock=" + mock + ", f=" + f + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;
        return coord.equals(region.coord);
    }

    @Override
    public int hashCode() {
        return coord.hashCode();
    }

    /**
     * Returns the region file associated with this region.
     *
     * @return the region file
     */
    File regionFile() {
        return f;
    }

    /**
     * Returns true if this is a mock (test) region.
     *
     * @return true if mock; false otherwise
     */
    boolean isMock() {
        return mock;
    }

    /**
     * Returns the last modified timestamp for the backing region file.
     * If there is no backing file (for example, unit tests running in
     * mock mode) then 0 will be returned.
     *
     * @return last modified timestamp
     */
    long modified() {
        return f == null ? 0 : f.lastModified();
    }

    /**
     * Returns a coordinate instance for this region.
     *
     * @return the coordinates
     */
    public Coord coord() {
        return coord;
    }

    /**
     * Returns the filename to be used for the tile image.
     *
     * @return the tile name
     */
    @Deprecated
    String tileName() {
        return T_DOT + coord.x() + DOT + coord.z() + DOT_PNG;
    }

    /**
     * Returns an input stream for the chunk data located at the given
     * coordinates within this region. If no chunk data exists at the given
     * coordinates, or if this region does not have a file associated with it,
     * null is returned.
     *
     * @param cx chunk x-coord
     * @param cz chunk z-coord
     * @return a data input stream for the chunk data, or null
     */
    DataInputStream getChunkDataStream(int cx, int cz) {
        return rf == null ? null : rf.getChunkDataStream(cx, cz);
    }

    /**
     * Returns the number of chunks contained in this region.
     *
     * @return the number of chunks in the region
     */
    int chunkCount() {
        return rf == null ? 0 : rf.chunkCount();
    }

    /**
     * Instructs the region to release any resources used to hold region data,
     * since we have generated the image and the data is no longer required.
     */
    void releaseResources() {
        rf = null;
    }
}
