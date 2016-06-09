/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Encapsulates min and max X and Z values.
 *
 * @author Simon Hunt
 */
class Bounds {
    private static final int ZERO = 0;

    private int minX = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int minZ = Integer.MAX_VALUE;
    private int maxZ = Integer.MIN_VALUE;

    private boolean noData = true;

    /**
     * Expands the current bounds to include the specified point.
     *
     * @param x x-coord
     * @param z z-coord
     */
    public void add(int x, int z) {
        noData = false;
        if (x < minX)
            minX = x;
        if (x > maxX)
            maxX = x;
        if (z < minZ)
            minZ = z;
        if (z > maxZ)
            maxZ = z;
    }

    /**
     * Expands the current bounds to include the specified point.
     *
     * @param coord x,z coordinates
     */
    public void add(Coord coord) {
        add(coord.x(), coord.z());
    }

    @Override
    public String toString() {
        return "Bounds{[" + minX() + "," + minZ() + "]x[" +
                maxX() + "," + maxZ() + "] " +
                "w=" + nx() + ", h=" + nz() + ", dim=" + maxDim() + "}";
    }

    /**
     * Returns the width of the bounds.
     *
     * @return number of X elements
     */
    int nx() {
        return noData ? ZERO : maxX - minX + 1;
    }

    /**
     * Returns the height of the bounds.
     *
     * @return number of Z elements
     */
    int nz() {
        return noData ? ZERO : maxZ - minZ + 1;
    }

    /**
     * Returns the maximum dimension.
     *
     * @return the maximum dimension
     */
    int maxDim() {
        return noData ? ZERO : (nx() > nz() ? nx() : nz());
    }

    /**
     * Returns the minimum X-value.
     *
     * @return min X
     */
    int minX() {
        return noData ? ZERO : minX;
    }

    /**
     * Returns the maximum X-value.
     *
     * @return max X
     */
    int maxX() {
        return noData ? ZERO : maxX;
    }

    /**
     * Returns the minimum Z-value.
     *
     * @return min Z
     */
    int minZ() {
        return noData ? ZERO : minZ;
    }

    /**
     * Returns the maximum Z-value.
     *
     * @return max Z
     */
    int maxZ() {
        return noData ? ZERO : maxZ;
    }
}
