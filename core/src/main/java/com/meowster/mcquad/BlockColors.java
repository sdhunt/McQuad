/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates block color data. When color information about a block
 * is requested, any unmapped block IDs will be logged, and the default
 * block color data will be returned.
 *
 * @author Simon Hunt
 */
class BlockColors {
    private static final String E_NO_DEFAULT = "No default block record found";
    private static final int MAX_DV = 16;

    private final Block defaultBlock;
    private final Map<BlockId, Block> cache = new HashMap<>();
    private final Map<BlockId, Boolean> opaque = new HashMap<>();
    private final Set<BlockId> defaulted = new TreeSet<>();

    /**
     * Creates a block color data instance.
     */
    BlockColors() {
        // create a block colors store from the text configuration file
        BlockColorsStore store = new BlockColorsStore();

        // encapsulate each block record in a block instance, and
        // create the lookup maps, indexed by block id
        Block defaultFound = null;
        for (Record r : store.getRecords()) {
            Block b = new Block((BlockColorsStore.BlockRecord) r);
            if (b.blockId() == null) {
                defaultFound = b;
                continue;
            }

            indexBlock(b);
        }

        if (defaultFound == null)
            throw new IllegalStateException(E_NO_DEFAULT);
        defaultBlock = defaultFound;
    }

    private void indexBlock(Block b) {
        BlockId blockId = b.blockId();
        // if data value is not applicable, store an entry in the cache for
        // every possible data value, so we can still do a fast lookup..
        if (blockId.dv() == BlockId.DV_NA)
            for (BlockId replicatedId : replicateIds(blockId))
                indexUnder(replicatedId, b);
        else
            indexUnder(blockId, b);
    }

    private List<BlockId> replicateIds(BlockId blockId) {
        final int id = blockId.id();
        List<BlockId> reps = new ArrayList<>();
        for (int dv = 0; dv < MAX_DV; dv++)
            reps.add(new BlockId(id, dv));
        return reps;
    }

    private void indexUnder(BlockId blockId, Block b) {
        cache.put(blockId, b);
        opaque.put(blockId, b.isFullyOpaque());
    }

    @Override
    public String toString() {
        return "BlockColors{size=" + cache.size() +
                ", #defaulted=" + defaulted.size() +
                "}";
    }

    /**
     * Returns the default block data.
     *
     * @return the default block
     */
    Block getDefaultBlock() {
        return defaultBlock;
    }

    /**
     * Returns the block with the given block ID (and data value 0). If no
     * such block is cached, the ID is added to the defaulted set, and the
     * default block is returned.
     *
     * @param id the block id
     * @return the corresponding block (or the default block)
     */
    Block getBlock(int id) {
        return getBlock(id, 0);
    }

    /**
     * Returns the block with the given block ID and data value. If no
     * such block is cached, the ID is added to the defaulted set, and the
     * default block is returned.
     *
     * @param id the block id
     * @param dv the block data value
     * @return the corresponding block (or the default block)
     */
    Block getBlock(int id, int dv) {
        BlockId blockId = new BlockId(id, dv);
        Block b = cache.get(blockId);
        if (b == null) {
            defaulted.add(blockId);
            return defaultBlock;
        }
        return b;
    }

    /**
     * Returns true if the block with the given ID and data value is fully
     * opaque; that is, if its alpha component is 0xff.
     *
     * @param id the block id
     * @param dv the block data value
     * @return true if the corresponding block color is fully opaque
     */
    public boolean fullyOpaque(int id, int dv) {
        BlockId blockId = new BlockId(id, dv);
        return opaque.get(blockId);
    }

    /**
     * Returns a read-only view of the defaulted block IDs.
     *
     * @return the set of defaulted block IDs
     */
    Set<BlockId> getDefaulted() {
        return Collections.unmodifiableSet(defaulted);
    }


    /**
     * Our shared instance.
     */
    static final BlockColors BLOCK_DB = new BlockColors();
}
