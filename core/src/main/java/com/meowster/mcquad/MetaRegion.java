/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Captures meta information about a region.
 */
class MetaRegion {

    private static final Pattern P_REGION_RECORD =
            Pattern.compile("^(\\[-?\\d+,-?\\d+\\])\\s+(\\d+)\\s+(\\d+)");
    private static final String FMT_RECORD = "%s %d %d";


    private final Coord coord;

    private int ttl;
    private long touched;

    /**
     * Creates a new meta region instance.
     *
     * @param coord coordinates of region
     */
    MetaRegion(Coord coord) {
        this.coord = coord;
    }

    /**
     * Returns the coordinates for this meta region.
     *
     * @return the coordinates
     */
    public Coord coord() {
        return coord;
    }

    /**
     * Returns the TTL data associated with this meta region.
     *
     * @return the TTL
     */
    int ttl() {
        return ttl;
    }

    /**
     * Returns the modified timestamp associated with this meta region.
     *
     * @return the last modified timestamp
     */
    long touched() {
        return touched;
    }

    @Override
    public String toString() {
        return "MetaRegion{" + coord + ", ttl=" + ttl +
                ", touched=" + touched + "}";
    }

    /**
     * Sets the last modified data value.
     *
     * @param touched value to set
     * @return self, for chaining
     */
    MetaRegion setTouched(long touched) {
        this.touched = touched;
        return this;
    }

    /**
     * Sets the TTL data value.
     *
     * @param ttl value to set
     * @return self, for chaining
     */
    MetaRegion setTtl(int ttl) {
        this.ttl = ttl;
        return this;
    }

    /**
     * Decrements the TTL by 1.
     */
    void decTtl() {
        if (ttl > 0) {
            ttl--;
        }
    }

    /**
     * Returns this meta region as a record to be persisted.
     *
     * @return string record format
     */
    String stringRecord() {
        return String.format(FMT_RECORD, coord, ttl, touched);
    }

    /**
     * Parses the given string to instantiate a meta region instance.
     *
     * @param s string representation
     * @return corresponding instance
     */
    static MetaRegion metaRegion(String s) {
        Matcher m = P_REGION_RECORD.matcher(s);
        if (m.matches()) {
            Coord coord = Coord.coord(m.group(1));
            int ttl = Integer.valueOf(m.group(2));
            long touched = Long.valueOf(m.group(3));

            return new MetaRegion(coord)
                    .setTtl(ttl)
                    .setTouched(touched);
        }
        return null;
    }

}
