/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import static com.meowster.util.StringUtils.EOL;

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
    private final int baseZoom;
    private final int maxZoom;
    // offsets for the region data mapped into the quad data
    private final int xoff;
    private final int zoff;
    // coordinate calibration for origin
    private final int xcc;
    private final int zcc;
    private final Coord calibration;

    /**
     * Constructs quad data based on the specified region data.
     *
     * @param regionData the regionData on which to base the quad
     */
    public QuadData(RegionData regionData) {
        /*
         NOTE:
           Regions are composed of 512x512 blocks.
           Quads are composed of 256x256 pixels.

           So, the base-level quads are four per region, the coords of which
           are computed from the region (quad-based) coords [X, Z] as follows:

                    2X    2X+1
                 +------+------+
           2Z    |      |      |
                 |      |      |
                 +------+------+
           2Z+1  |      |      |
                 |      |      |
                 +------+------+
         */
        this.regionData = regionData;
        bounds = regionData.bounds();
        quadDim = computeQuad();
        baseZoom = computeBaseZoom(quadDim);
        maxZoom = baseZoom + 2;             // to provide 4x4 pixels per block
        xoff = computeOffset(bounds.nx());
        zoff = computeOffset(bounds.nz());
        xcc = bounds.minX() * 2 - xoff;
        zcc = bounds.minZ() * 2 - zoff;
        calibration = new Coord(xcc, zcc);
    }

    @Override
    public String toString() {
        return "QuadData{" +
                "quadDim=" + quadDim +
                ", baseZoom=" + baseZoom +
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
        // NOTE: regions are 512x512 blocks, but we want tiles 256x256 so
        //       double the tiles per side...
        return q * 2;
    }

    // compute zoom level at which we have 256x256 blocks per tile
    private int computeBaseZoom(int quadDim) {
        int zoom = 0;
        int dim = quadDim;
        while (dim > 1) {
            zoom++;
            dim /= 2;
        }
        return zoom;
    }

    private int computeOffset(int n) {
        int pad = (quadDim/2) - n;

        // NOTE: don't try to simplify the following two lines, because we
        //       are relying on integer division..
        int off = pad / 2;

        // double to convert region coords to quad coords..
        off *= 2;
        return off;
    }

    private String tileChar(Region region) {
        return region == null ? " ."
                : (region.coord().equals(ORIGIN) ? " O" : " #");
    }


    private String charAt(int x, int z) {
        int rqx = x/2;
        int rqz = z/2;
        int cx = rqx + (xcc/2);
        int cz = rqz + (zcc/2);
        Region r = regionData.at(cx, cz);
        return tileChar(r);
    }

    /**
     * Produces a schematic showing how the regions map onto the
     * quad tiling grid.
     *
     * @return a schematic
     */
    public String schematic() {
        StringBuilder sb = new StringBuilder();
        for (int z=0; z<quadDim; z+=2) {
            for (int x=0; x<quadDim; x+=2) {
                sb.append(charAt(x, z));
            }
            sb.append(EOL);
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
        Coord xz = quadToRegion(a/2, b/2);
        return regionData.at(xz);
    }

    /**
     * Converts quad coordinates to region coordinates.
     *
     * @param a a-coord
     * @param b b-coord
     * @return the corresponding region coordinates
     */
    private Coord quadToRegion(int a, int b) {
        return new Coord(a + xcc/2, b + zcc/2);
    }

    /**
     * Returns the base zoom level; that is, the zoom level at which
     * the resolution is one pixel per block.
     *
     * @return the base zoom level
     */
    public int baseZoom() {
        return baseZoom;
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
