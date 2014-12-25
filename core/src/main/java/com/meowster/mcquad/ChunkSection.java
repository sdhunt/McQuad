/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import static com.meowster.mcquad.NbtUtils.*;

/**
 * Encapsulates data about a chunk section (16x16x16 blocks).
 *
 * @author Simon Hunt
 */
public class ChunkSection {

    private static final int CUBE_16 = 16 * 16 * 16;
    private static final int NBLOCKS = 16;
    private static final int FF = 0xff;

    private final BlockData[] blockData = new BlockData[CUBE_16];

    /**
     * Creates the chunk section from the specified (NBT compound) section tag.
     *
     * @param sectionTag the section tag
     * @param biomeData the biome data associated with this chunk
     */
    public ChunkSection(CompoundTag sectionTag, ChunkBiomeData biomeData) {
        byte[] idsLow = getByteArrayFromTag(sectionTag, BLOCKS);
        Tag addTag = sectionTag.getValue().get(ADD);
        byte[] add = addTag == null ? null : ((ByteArrayTag) addTag).getValue();
        byte[] data = getByteArrayFromTag(sectionTag, DATA);

        if (add != null) {
            process(idsLow, add, data, biomeData);
        } else {
            process(idsLow, data, biomeData);
        }
    }

    private void process(byte[] idsLow, byte[] add, byte[] data,
                         ChunkBiomeData biomeData) {
        for (int index = 0; index < CUBE_16; index++) {
            short blockId = (short) (idsLow[index] & FF);
            blockId |= nybble(add, index) << 8;
            byte datum = nybble(data, index);
            Biome biome = biomeData.biomeAt(index);
            blockData[index] = new BlockData(blockId, datum, biome);
        }
    }

    private void process(byte[] idsLow, byte[] data,
                         ChunkBiomeData biomeData) {
        for (int index = 0; index < CUBE_16; index++) {
            short blockId = (short) (idsLow[index] & FF);
            byte datum = nybble(data, index);
            Biome biome = biomeData.biomeAt(index);
            blockData[index] = new BlockData(blockId, datum, biome);
        }
    }

    /**
     * Extract a 4-bit value from a byte in an array, where the first nybble
     * in each byte (even nybble indexes) occupies the lower 4 bits and the
     * second (odd nybble indexes) occupies the high bits.
     *
     * @param arr the source array
     * @param index the index (in nybbles) of the desired 4 bits
     * @return the desired 4 bits as the lower bits of a byte
     */
    private byte nybble(byte[] arr, int index) {
        byte data = arr[index/2];
        if (index % 2 == 1)
            data >>= 4;
        return (byte) (data & 0xf);
    }

    /**
     * Returns the block data for the given coordinates [x,y,z].
     *
     * @param x the x-coord (0..15)
     * @param y the y-coord (0..15)
     * @param z the z-coord (0..15)
     * @return the block at those coordinates
     */
    public BlockData blockAt(int x, int y, int z) {
        return blockData[index(x, y, z)];
    }

    /**
     * Returns the (local) y-value (0..15) of the highest fully opaque block
     * at [x,z]. If none are found, -1 is returned.
     *
     * @param x the x-coord
     * @param z the z-coord
     * @return the y value of the highest fully opaque block at those coords
     */
    public int highOpaque(int x, int z) {
        for (int y = NBLOCKS-1; y >= 0; y--) {
            BlockData block = blockAt(x, y, z);
            if (block.isFullyOpaque())
                return y;
        }
        return -1;
    }

    private int index(int x, int y, int z) {
        return y * NBLOCKS * NBLOCKS + z * NBLOCKS + x;
    }

}
