/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
 * Encapsulates an instance of a block in a section of a chunk.
 *
 * @author Simon Hunt
 */
public class BlockData {
    private static final int FF = 0xff;

    /**
     * Alpha below which blocks are considered transparent for purposes of
     * shading (i.e. blocks with alpha < this will not be shaded)
     */
    private static final int SHADE_OPACITY_CUTOFF = 0x20;

    private final Block block;
    private final Biome biome;
    private final Color computedColor;

    /**
     * Constructs a block data instance that fully describes a block at
     * a particular [x,y,z] location in the chunk-section / chunk.
     *
     * @param id the block ID
     * @param data the block data value
     * @param biome the biome the block belongs in
     */
    public BlockData(short id, byte data, Biome biome) {
        int dv = data & FF; // convert byte value to 0..255
        block = BlockColors.BLOCK_DB.getBlock(id, dv);
        this.biome = biome;
        computedColor = computeColor();
    }

    /**
     * Constructs a block data instance representing an air block.
     */
    public BlockData() {
        biome = BiomeColors.BIOME_DB.getDefaultBiome();
        block = BlockColors.BLOCK_DB.getBlock(0); // AIR
        computedColor = Color.TRANSPARENT;
    }

    /**
     * Computes the color of this block, using block data and biome influence.
     *
     * @return the computed color
     */
    private Color computeColor() {
        Color c = block.color();
        Color influenceColor = biome.influence(block.biomeInfluence());
        return c.multiplySolid(influenceColor);
    }

    /**
     * Returns the computed color for this block, taking biome influence
     * into consideration.
     *
     * @return the computed color of the block
     */
    public Color computedColor() {
        return computedColor;
    }

    /**
     * Returns true if the block is fully opaque; that is to say, the block
     * color has an alpha component of {@code 0xff}.
     *
     * @return true if this block is fully opaque
     */
    public boolean isFullyOpaque() {
        return block.isFullyOpaque();
    }

    /**
     * Returns true if the computed block color has enough opacity to warrant
     * registering as a solid block, in the context of the "height" and
     * "shading" algorithms.
     *
     * @return true if this block is opaque enough
     */
    public boolean opaqueEnough() {
        return computedColor.alpha() >= SHADE_OPACITY_CUTOFF;
    }
}
