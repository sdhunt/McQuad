/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import static com.meowster.mcquad.NbtUtils.BIOMES;
import static com.meowster.mcquad.NbtUtils.LEVEL;
import static com.meowster.mcquad.NbtUtils.SECTIONS;
import static com.meowster.mcquad.NbtUtils.Y;
import static com.meowster.mcquad.NbtUtils.getByteArrayFromTag;
import static com.meowster.mcquad.NbtUtils.getCompoundTagFromTag;
import static com.meowster.mcquad.NbtUtils.getIntFromTag;
import static com.meowster.mcquad.NbtUtils.getTagListFromTag;
import static com.meowster.mcquad.NbtUtils.tagExists;

/**
 * Encapsulates the data associated with a chunk.
 *
 * @author Simon Hunt
 */
class Chunk {
    private static final int NSECTIONS = 16;
    private static final int NBLOCKS = 16;

    private static final BlockData AIR_BLOCK = new BlockData();


    private Color[][] surfaceColors = new Color[NBLOCKS][NBLOCKS];
    private short[][] surfaceHeights = new short[NBLOCKS][NBLOCKS];

    private ChunkSection[] sections = new ChunkSection[NSECTIONS];

    private int highestSection;

    /**
     * Constructs a chunk from the given data input stream.
     *
     * @param dis the source data
     */
    Chunk(DataInputStream dis) {
        NBTInputStream nis = null;
        try {
            nis = new NBTInputStream(dis);
            loadChunkData(getLevelTag(nis));
        } catch (IOException e) {
            e.printStackTrace();  // TODO should we leave this here?
        } finally {
            if (nis != null) {
                try {
                    nis.close();
                } catch (IOException e) {
                    // really, what are we going to do?
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Chunk{" + sectionMap() + "}";
    }

    private String sectionMap() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NSECTIONS; i++)
            sb.append(sections[i] != null ? "#" : ".");
        return sb.toString();
    }

    private CompoundTag getLevelTag(NBTInputStream nis) throws IOException {
        CompoundTag rootTag = (CompoundTag) nis.readTag();
        return getCompoundTagFromTag(rootTag, LEVEL);
    }

    private void loadChunkData(CompoundTag levelTag) {
        // first, grab the biome data for the chunk...
        ChunkBiomeData biomeData = loadBiomeData(levelTag);

        // then grab all the defined sections...
        for (Tag t : getSectionTags(levelTag)) {
            CompoundTag sectionTag = (CompoundTag) t;
            int sIdx = getSectionIndex(sectionTag);
            sections[sIdx] = new ChunkSection(sectionTag, biomeData);
        }

        // remember the highest "used" section
        highestSection = highestUsedSection();
    }

    private ChunkBiomeData loadBiomeData(CompoundTag levelTag) {
        if (!tagExists(levelTag, BIOMES))
            return new ChunkBiomeData();
        return new ChunkBiomeData(getByteArrayFromTag(levelTag, BIOMES));
    }

    private List<Tag> getSectionTags(CompoundTag levelTag) {
        return getTagListFromTag(levelTag, SECTIONS);
    }

    private int getSectionIndex(CompoundTag t) {
        return getIntFromTag(t, Y);
    }

    /**
     * Returns a reference to our computed surface colors.
     *
     * @return the surface colors
     */
    Color[][] getSurfaceColors() {
        return surfaceColors;
    }

    /**
     * Returns a reference to our computed surface heights.
     *
     * @return the surface heights
     */
    short[][] getSurfaceHeights() {
        return surfaceHeights;
    }

    /**
     * Computes the surface colors and heights based on the loaded data.
     */
    void computeColorsAndHeights() {
        // iterate over each vertical stack of blocks...
        for (int z = 0; z < NBLOCKS; z++) {
            for (int x = 0; x < NBLOCKS; x++) {

                // Find the location of the highest fully-opaque block at [x,z].
                // Worst case is that we'll hit bedrock, which is opaque,
                // so we'll always have a y-value >= 0.
                int yOpaque = highestOpaqueBlock(x, z, highestSection);
                int height = yOpaque;
                BlockData block = blockAt(x, yOpaque, z);

                // We start with the color of that block, then overlay the
                // colors of the blocks above it, until we hit the top of
                // the highest populated chunk section
                Color pixelColor = block.computedColor();
                final int maxY = (highestSection + 1) * NBLOCKS;

                for (int y = yOpaque + 1; y < maxY; y++) {
                    BlockData b = blockAt(x, y, z);
                    Color c = b.computedColor();
                    pixelColor = pixelColor.overlay(c);
                    // while we are here, see if the the overlay block is
                    // opaque enough to increase our height marker...
                    if (b.opaqueEnough())
                        height = y;
                }

                // and while we are at it, demultiply alpha as the last step.
                surfaceColors[z][x] = pixelColor.demultiplyAlpha();
                surfaceHeights[z][x] = (short) height;
            }
        }
    }


    private BlockData blockAt(int x, int y, int z) {
        int sIdx = y / NSECTIONS;
        int localY = y % NSECTIONS;
        ChunkSection s = sections[sIdx];
        return s == null ? AIR_BLOCK : s.blockAt(x, localY, z);
    }

    private int highestOpaqueBlock(int x, int z, int highestSection) {
        int highestOpaque = -1;
        for (int sIdx = highestSection; sIdx >= 0; sIdx--) {
            ChunkSection s = sections[sIdx];
            if (s != null) {
                int high = s.highOpaque(x, z);
                if (high > -1)
                    highestOpaque = high + sIdx * NBLOCKS;
            }
            if (highestOpaque > -1)
                break;
        }
        return highestOpaque;
    }

    private int highestUsedSection() {
        int highest = NSECTIONS - 1;
        while (sections[highest] == null)
            highest--;
        return highest;
    }
}
