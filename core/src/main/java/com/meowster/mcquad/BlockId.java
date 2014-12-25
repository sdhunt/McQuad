/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.StringUtils;

/**
 * Uniquely identifies a block, based on the block ID (0..4095) and
 * data value (0..15). Note that we can create any valid combination, even
 * if such a block does not exist in Minecraft yet. Note that we can also
 * create a block id where the data value is not applicable.
 *
 * @author Simon Hunt
 */
public class BlockId implements Comparable<BlockId> {
    /** Data value denoting "not applicable" */
    public static final int DV_NA = -1;

    private static final int FF = 0xff;
    private static final int MAX_ID = 4095;
    private static final int MAX_DV = 15;
    private static final String E_OOB = "Out of bounds: ";
    private static final String COLON = ":";

    private final int id;
    private final int dv;
    private final int hashCode;

    /**
     * Creates a block ID with the given ID and data value. Note that IDs must
     * fall in the range 0..4095 and data values must fall in the range -1..15,
     * where -1 denotes "not applicable".
     *
     * @param id the block ID
     * @param dv the block data value
     * @throws IllegalArgumentException if either value is out of bounds
     */
    public BlockId(int id, int dv) {
        validate(id, dv);
        this.id = id;
        this.dv = dv;
        this.hashCode = (id << 8) | (dv & FF);
    }

    private void validate(int id, int dv) {
        if (id < 0 || id > MAX_ID || dv < DV_NA || dv > MAX_DV)
            throw new IllegalArgumentException(E_OOB + id + COLON + dv);
    }

    /**
     * Returns the ID.
     *
     * @return the ID
     */
    public int id() {
        return id;
    }

    /**
     * Returns the data value.
     *
     * @return the data value
     */
    public int dv() {
        return dv;
    }

    @Override
    public String toString() {
        String dvStr = dv == DV_NA ? "-" : Integer.toString(dv);
        return "BlockId{" + StringUtils.asHex(id) + ":" + dvStr + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockId blockId = (BlockId) o;
        return dv == blockId.dv && id == blockId.id;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(BlockId o) {
        return this.hashCode - o.hashCode;
    }
}
