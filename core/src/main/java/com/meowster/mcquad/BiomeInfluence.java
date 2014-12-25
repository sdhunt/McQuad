/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Denotes a biome influence type which is used during block color selection.
 *
 * @author Simon Hunt
 */
public enum BiomeInfluence {
    NONE,
    GRASS,
    WATER,
    FOLIAGE;

    /**
     * Returns the biome constant for the given string.
     *
     * @param s the string name
     * @return the constant
     */
    public static BiomeInfluence from(String s) {
        return s == null ? NONE : valueOf(s.toUpperCase());
    }
}
