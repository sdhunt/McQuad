/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.Record;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates biome color data. When color information about a biome is
 * requested, any unmapped biome IDs will be logged, and the default
 * biome color data will be returned.
 *
 * @author Simon Hunt
 */
class BiomeColors {
    private static final String E_NO_DEFAULT = "No default biome record found";

    private final Biome defaultBiome;
    private final Map<BiomeId, Biome> cache = new HashMap<>();
    private final Set<BiomeId> defaulted = new TreeSet<>();

    /**
     * Creates a biome color data instance.
     */
    BiomeColors() {
        // create a biome colors store from the text configuration file
        BiomeColorsStore store = new BiomeColorsStore();

        // encapsulate each biome record in a biome instance, and
        // create the lookup map, indexed by biome id
        Biome defaultFound = null;
        for (Record r : store.getRecords()) {
            Biome b = new Biome((BiomeColorsStore.BiomeRecord) r);
            if (b.biomeId() == null) {
                defaultFound = b;
                continue;
            }

            cache.put(b.biomeId(), b);
        }

        if (defaultFound == null)
            throw new IllegalStateException(E_NO_DEFAULT);
        defaultBiome = defaultFound;
    }

    @Override
    public String toString() {
        return "BiomeColors{size=" + cache.size() +
                ", #defaulted=" + defaulted.size() +
                "}";
    }

    /**
     * Returns the default biome data.
     *
     * @return the default biome
     */
    Biome getDefaultBiome() {
        return defaultBiome;
    }

    /**
     * Returns the biome with the given biome ID. If no such biome is
     * cached, the ID is added to the defaulted set, and the default biome
     * is returned.
     *
     * @param id the biome id
     * @return the corresponding biome (or the default biome)
     */
    Biome getBiome(int id) {
        BiomeId biomeId = new BiomeId(id);
        Biome b = cache.get(biomeId);
        if (b == null) {
            defaulted.add(biomeId);
            return defaultBiome;
        }
        return b;
    }


    /**
     * Returns a read-only view of the defaulted biome IDs.
     *
     * @return the set of defaulted biome IDs
     */
    Set<BiomeId> getDefaulted() {
        return Collections.unmodifiableSet(defaulted);
    }


    /**
     * Our shared instance.
     */
    static final BiomeColors BIOME_DB = new BiomeColors();
}
