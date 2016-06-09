/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Denotes data about a block.
 *
 * @author Simon Hunt
 */
class Block {
    private static final int FF = 0xff;

    private final BlockColorsStore.BlockRecord record;
    private final boolean fullyOpaque;

    /**
     * Constructs a block from a block record.
     *
     * @param br the block record
     */
    Block(BlockColorsStore.BlockRecord br) {
        record = br;
        fullyOpaque = br.color().alpha() == FF;
    }

    /**
     * Returns the block ID for this block.
     *
     * @return the block ID
     */
    BlockId blockId() {
        return record.blockId();
    }

    /**
     * Returns the color of the block.
     *
     * @return the block color
     */
    public Color color() {
        return record.color();
    }

    /**
     * Returns the biome influence of the block (for coloring).
     *
     * @return the block biome influence
     */
    BiomeInfluence biomeInfluence() {
        return record.biomeInfluence();
    }

    /**
     * Returns the comment for the block.
     *
     * @return the comment
     */
    public String comment() {
        return record.comment();
    }

    /**
     * Returns true if the {@link #color} of this block is fully opaque; that is,
     * if the alpha component of the color is {@code 0xff}.
     *
     * @return true if this block is fully opaque
     */
    boolean isFullyOpaque() {
        return fullyOpaque;
    }

    @Override
    public String toString() {
        return "Block{" + record + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;
        return blockId().equals(block.blockId());
    }

    @Override
    public int hashCode() {
        return blockId().hashCode();
    }
}
