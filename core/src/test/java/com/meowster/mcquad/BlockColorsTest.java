/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link com.meowster.mcquad.BlockColors}.
 *
 * @author Simon Hunt
 */
public class BlockColorsTest extends AbstractTest {

    private BlockColors bc;
    private Block b;
    private Block b2;

    @Before
    public void setUp() {
        bc = new BlockColors();
    }

    @Test
    public void basic() {
        title("basic");
        print(bc);
        assertEquals(AM_UXS, 0, bc.getDefaulted().size());
    }

    @Test
    public void notMapped() {
        title("notMapped");
        b = bc.getBlock(2000);
        b2 = bc.getDefaultBlock();
        assertEquals(AM_NEQ, b2, b);
        Set<BlockId> def = bc.getDefaulted();
        print(def);
        assertEquals(AM_UXS, 1, def.size());
        assertTrue(AM_HUH, def.contains(new BlockId(2000, 0)));
    }

    @Test
    public void defaultBlock() {
        title("defaultBlock");
        b = bc.getDefaultBlock();
        print(b);
        assertNull(AM_HUH, b.blockId());
        assertEquals(AM_NEQ, Color.MAGENTA, b.color());
        assertEquals(AM_NEQ, BiomeInfluence.NONE, b.biomeInfluence());
        assertTrue(AM_HUH, b.isFullyOpaque());
    }

    @Test
    public void air() {
        title("air");
        b = bc.getBlock(0);
        print(b);
        assertEquals(AM_UXS, 0, bc.getDefaulted().size());
        assertEquals(AM_NEQ, Color.TRANSPARENT, b.color());
        assertEquals(AM_NEQ, BiomeInfluence.NONE, b.biomeInfluence());
        assertEquals(AM_NEQ, "air", b.comment());
    }

    @Test
    public void grass() {
        title("grass");
        b = bc.getBlock(2);
        print(b);
        assertEquals(AM_NEQ, new Color(0xff939393), b.color());
        assertEquals(AM_NEQ, BiomeInfluence.GRASS, b.biomeInfluence());
        assertEquals(AM_NEQ, "grass block", b.comment());
    }

    @Test
    public void dirt() {
        title("dirt");
        b = bc.getBlock(3);
        print(b);
        assertEquals(AM_NEQ, new Color(0xff866043), b.color());
        assertEquals(AM_NEQ, BiomeInfluence.NONE, b.biomeInfluence());
        assertEquals(AM_NEQ, "dirt", b.comment());
    }

}
