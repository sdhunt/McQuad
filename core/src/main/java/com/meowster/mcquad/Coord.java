/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An immutable class that combines x and z coordinates into a single key.
 */
public class Coord implements Comparable<Coord> {

    private static final Pattern RE_COORD =
            Pattern.compile("\\[(-?\\d+),\\s*(-?\\d+)\\]");

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
        return 31 * x + z;
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

    /**
     * Returns the coordinates that are scaled by the given factor.
     * That is, returns {@code [x*s, z*s]};
     *
     * @param s the scale factor
     * @return these coordinates scaled by the given amount
     */
    public Coord scale(int s) {
        return new Coord(x * s, z * s);
    }

    /**
     * Returns the coordinates computed by dividing these coordinates
     * by 2 (integer division).
     *
     * @return these coordinates scaled by half
     */
    public Coord div2() {
        return new Coord(x / 2, z / 2);
    }

    @Override
    public int compareTo(Coord o) {
        int result = this.x - o.x;
        if (result == 0) {
            result = this.z - o.z;
        }
        return result;
    }

    /**
     * Returns the coordinate that is this plus one to the x element.
     *
     * @return this coord (x+1, z)
     */
    public Coord xPlus1() {
        return new Coord(x + 1, z);
    }

    /**
     * Returns the coordinate that is this plus one to the z element.
     *
     * @return this coord (x, z+1)
     */
    public Coord zPlus1() {
        return new Coord(x, z + 1);
    }

    /**
     * Returns the coordinate that is this plus one to both x and z elements.
     *
     * @return this coord (x+1, z+1)
     */
    public Coord xzPlus1() {
        return new Coord(x + 1, z + 1);
    }

    /**
     * Parses the given string, expected to be of the form:
     * <pre>
     *     [ {x}, {z} ]
     * </pre>
     * where {x} and {z} are integers.
     *
     * @param s string to parse
     * @return corresponding coordinates instance
     * @throws IllegalArgumentException if string is not parsable
     */
    public static Coord coord(String s) {
        Matcher m = RE_COORD.matcher(s);
        if (m.matches()) {
            int x = Integer.valueOf(m.group(1));
            int z = Integer.valueOf(m.group(2));
            return new Coord(x, z);
        }
        throw new IllegalArgumentException("Unable to parse coords: " + s);
    }
}
