/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import java.util.List;

/**
 * Convenience methods for dealing with NBT tags.
 *
 * @author Simon Hunt
 */
class NbtUtils {
    static final String LEVEL = "Level";
    static final String BIOMES = "Biomes";
    static final String SECTIONS = "Sections";
    static final String Y = "Y";
    static final String BLOCKS = "Blocks";
    static final String DATA = "Data";
    static final String ADD = "Add";

    /**
     * Returns true if a sub tag with the given key exists on the specified
     * NBT compound tag.
     *
     * @param tag the tag instance
     * @param key the key for the sub tag
     * @return true if the sub tag exists
     */
    static boolean tagExists(CompoundTag tag, String key) {
        return tag.getValue().get(key) != null;
    }

    /**
     * Extracts a sub tag from an NBT compound tag, where the child
     * element is assumed to be a {@link org.jnbt.CompoundTag}.
     *
     * @param tag the tag instance
     * @param key the key for the sub tag
     * @return the byte array
     */
    static CompoundTag getCompoundTagFromTag(CompoundTag tag, String key) {
        return (CompoundTag) tag.getValue().get(key);
    }

    /**
     * Extracts a sub tag from an NBT compound tag, where the child
     * element is assumed to be a {@link org.jnbt.ByteArrayTag}.
     *
     * @param tag the tag instance
     * @param key the key for the byte array attribute
     * @return the byte array
     */
    private static ByteArrayTag getByteArrayTagFromTag(CompoundTag tag, String key) {
        return (ByteArrayTag) tag.getValue().get(key);
    }

    /**
     * Extracts a byte array from an NBT compound tag, where the child
     * element is assumed to be a {@link org.jnbt.ByteArrayTag}.
     *
     * @param tag the tag instance
     * @param key the key for the byte array attribute
     * @return the byte array
     */
    static byte[] getByteArrayFromTag(CompoundTag tag, String key) {
        return getByteArrayTagFromTag(tag, key).getValue();
    }

    /**
     * Extracts an integer from an NBT compound tag, where the child
     * element is assumed to be a {@link org.jnbt.ByteTag}.
     *
     * @param tag the tag instance
     * @param key the key for the byte array attribute
     * @return the byte array
     */
    static int getIntFromTag(CompoundTag tag, String key) {
        return ((ByteTag) tag.getValue().get(key)).getValue().intValue();
    }

    /**
     * Extracts a list of tags from an NBT compound tag, where the child
     * element is assumed to be a {@link org.jnbt.ListTag}.
     *
     * @param tag the tag instance
     * @param key the key for the byte array attribute
     * @return the byte array
     */
    static List<Tag> getTagListFromTag(CompoundTag tag, String key) {
        return ((ListTag) tag.getValue().get(key)).getValue();
    }

}
