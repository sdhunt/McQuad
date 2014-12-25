/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Denotes data about a biome.
 *
 * @author Simon Hunt
 */
public class Biome {
    private final BiomeColorsStore.BiomeRecord record;

    /**
     * Constructs a biome from a biome record.
     *
     * @param br the biome record
     */
    Biome(BiomeColorsStore.BiomeRecord br) {
        record = br;
    }

    /**
     * Returns the biome ID for this biome.
     *
     * @return the biome ID
     */
    public BiomeId biomeId() {
        return record.biomeId();
    }

    /**
     * Returns the influence color for this biome, for the given
     * influence type.
     *
     * @param influence biome influence type
     * @return the influence color
     */
    public Color influence(BiomeInfluence influence) {
        switch (influence) {
            case GRASS: return record.grass();
            case WATER: return record.water();
            case FOLIAGE: return record.foliage();
            default: return Color.WHITE;  // no influence
        }
    }

    /**
     * Returns the comment for the biome.
     *
     * @return the comment
     */
    public String comment() {
        return record.comment();
    }

    @Override
    public String toString() {
        return "Biome{" + record + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Biome biome = (Biome) o;
        return biomeId().equals(biome.biomeId());
    }

    @Override
    public int hashCode() {
        return biomeId().hashCode();
    }
}
