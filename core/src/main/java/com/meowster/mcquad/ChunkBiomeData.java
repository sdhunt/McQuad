/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Encapsulates biome data specific to a chunk.
 *
 * @author Simon Hunt
 */
class ChunkBiomeData {

    private static final int FF = 0xff;
    private static final int NBLOCKS = 16;
    private static final int SQUARE_NBLOCKS = NBLOCKS * NBLOCKS;

    private final byte[] data;

    /**
     * Constructs an empty chunk biome data instance.
     */
    ChunkBiomeData() {
        data = null;
    }

    /**
     * Constructs the chunk biome data from the given byte array.
     *
     * @param data the biome data
     */
    ChunkBiomeData(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the biome for the given Y-Z-X coordinates encoded in the
     * specified integer.
     *
     * @param yzxIndex the Y-Z-X coordinates
     * @return the biome for the corresponding [x,z] coordinates
     */
    Biome biomeAt(int yzxIndex) {
        int xz = yzxIndex % SQUARE_NBLOCKS;
        return data == null ? BiomeColors.BIOME_DB.getDefaultBiome()
                : BiomeColors.BIOME_DB.getBiome(data[xz] & FF);
    }
}
