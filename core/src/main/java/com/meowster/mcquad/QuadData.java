/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Encapsulates data computed for the quad map.
 *
 * @author Simon Hunt
 */
public class QuadData {

    private static final Coord ORIGIN = new Coord(0, 0);

    private final RegionData regionData;
    private final Bounds bounds;

    private final int quadDim;
    private final int maxZoom;
    // offsets for the region mapped into the quad
    private final int xoff;
    private final int zoff;
    // coordinate calibration
    private final int xcc;
    private final int zcc;
    private final Coord calibration;

    /**
     * Constructs quad data based on the specified region data.
     *
     * @param regionData the regionData on which to base the quad
     */
    public QuadData(RegionData regionData) {
        this.regionData = regionData;
        bounds = regionData.bounds();
        quadDim = computeQuad();
        maxZoom = computeMaxZoom(quadDim);
        xoff = computeOffset(bounds.nx());
        zoff = computeOffset(bounds.nz());
        xcc = bounds.minX() - xoff;
        zcc = bounds.minZ() - zoff;
        calibration = new Coord(xcc, zcc);
    }

    @Override
    public String toString() {
        return "QuadData{" +
                "quadDim=" + quadDim +
                ", maxZoom=" + maxZoom +
                ", xoff=" + xoff +
                ", zoff=" + zoff +
                ", cal=" + calibration +
                '}';
    }

    private int computeQuad() {
        int q = 1;
        final int dim = bounds.maxDim();
        while (q < dim)
            q *= 2;
        return q;
    }

    private int computeMaxZoom(int quadDim) {
        int zoom = 0;
        int dim = quadDim;
        while (dim > 1) {
            zoom++;
            dim /= 2;
        }
        return zoom;
    }

    private int computeOffset(int n) {
        int pad = quadDim - n;
        return pad / 2;
    }

    private String tileChar(Region region) {
        return region == null ? " ."
                : (region.coord().equals(ORIGIN) ? " O" : " #");
    }

    /**
     * Produces a schematic showing the quad tiling and which regions
     * have data defined.
     *
     * @return a schematic
     */
    public String schematic() {
        StringBuilder sb = new StringBuilder();
        for (int z=0; z<quadDim; z++) {
            for (int x=0; x<quadDim; x++) {
                sb.append(tileChar(regionData.at(x + xcc, z + zcc)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns the associated region data.
     *
     * @return the region data
     */
    public RegionData regionData() {
        return regionData;
    }

    /**
     * Returns the region at location [a,b] in the quad, or null if no
     * region mapped there.
     *
     * @param a a-coord
     * @param b b-coord
     * @return the region mapped at [a,b]
     */
    public Region at(int a, int b) {
        Coord xz = quadToRegion(a, b);
        return regionData.at(xz.x(), xz.z());
    }

    /**
     * Converts quad coordinates to region coordinates.
     *
     * @param a a-coord
     * @param b b-coord
     * @return the corresponding region coordinates
     */
    private Coord quadToRegion(int a, int b) {
        return new Coord(a + xcc, b + zcc);
    }

    /**
     * Returns the maximum zoom level.
     *
     * @return the maximum zoom level
     */
    public int maxZoom() {
        return maxZoom;
    }

    /**
     * Returns the calibration data in the form of coordinates.
     *
     * @return the calibration data
     */
    public Coord calibration() {
        return calibration;
    }
}
