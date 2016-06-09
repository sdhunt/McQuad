/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.StringUtils;

/**
 * Uniquely identifies a biome, based on the biome ID (0..255). Note that we
 * can create any valid value, even if such a biome does not exist in
 * Minecraft yet.
 *
 * @author Simon Hunt
 */
class BiomeId implements Comparable<BiomeId> {
    private static final int MAX_ID = 255;
    private static final String E_OOB = "Out of bounds: ";

    private final int id;

    /**
     * Creates a biome ID with the given ID value. Note that IDs must
     * fall in the range 0..255.
     *
     * @param id the biome ID
     * @throws IllegalArgumentException if the ID is out of bounds
     */
    BiomeId(int id) {
        if (id < 0 || id > MAX_ID)
            throw new IllegalArgumentException(E_OOB + id);
        this.id = id;
    }

    /**
     * Returns the ID as an integer.
     *
     * @return the ID
     */
    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return "BiomeId{" + StringUtils.asHex(id) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BiomeId biomeId = (BiomeId) o;
        return id == biomeId.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(BiomeId o) {
        return this.id - o.id;
    }
}
