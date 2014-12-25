/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
 * An immutable class that combines x and z coordinates into a single key.
 */
public class Coord {
    private final int x;
    private final int z;

    /**
     * Constructs a coordinate instance for the given x and z components.
     *
     * @param x the x component
     * @param z the z component
     */
    public Coord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + x + "," + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coord coord = (Coord) o;
        return x == coord.x && z == coord.z;
    }

    @Override
    public int hashCode() {
        return  31 * x + z;
    }

    /**
     * Returns the x component.
     *
     * @return x
     */
    public int x() {
        return x;
    }

    /**
     * Returns the z component.
     *
     * @return z
     */
    public int z() {
        return z;
    }

    /**
     * Returns the coordinates that are the negation of this instance.
     * That is, returns {@code [-x, -z]};
     *
     * @return the negation of these coordinates
     */
    public Coord negation() {
        return new Coord(-x, -z);
    }
}
