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
 * Unit tests for {@link com.meowster.mcquad.BiomeColors}.
 *
 * @author Simon Hunt
 */
public class BiomeColorsTest extends AbstractTest {

    private BiomeColors bc;
    private Biome b;
    private Biome b2;

    @Before
    public void setUp() {
        bc = new BiomeColors();
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
        b = bc.getBiome(254);
        b2 = bc.getDefaultBiome();
        assertEquals(AM_NEQ, b2, b);
        Set<BiomeId> def = bc.getDefaulted();
        print(def);
        assertEquals(AM_UXS, 1, def.size());
        assertTrue(AM_HUH, def.contains(new BiomeId(254)));

    }

    @Test
    public void defaultBiome() {
        title("defaultBiome");
        b = bc.getDefaultBiome();
        print(b);
        assertNull(AM_HUH, b.biomeId());
        assertEquals(AM_NEQ, Color.MAGENTA, b.influence(BiomeInfluence.GRASS));
        assertEquals(AM_NEQ, Color.CYAN, b.influence(BiomeInfluence.FOLIAGE));
        assertEquals(AM_NEQ, Color.YELLOW, b.influence(BiomeInfluence.WATER));
    }

    @Test
    public void oceans() {
        title("oceans");
        checkBiomeData(0, "0xff8eb971", "0xff71a74d", "0xff0064e4", "oceans");
    }

    @Test
    public void extremeHills() {
        title("extremeHills");
        checkBiomeData(3, "0xff8ab689", "0xff6da36b", "0xff0028f4", "extreme hills");
    }

    private void checkBiomeData(int id, String gstr, String fstr, String wstr,
                                String cmt) {
        b = bc.getBiome(id);
        print(b);
        assertEquals(AM_NEQ, id, b.biomeId().id());
        assertEquals(AM_NEQ, new Color(gstr), b.influence(BiomeInfluence.GRASS));
        assertEquals(AM_NEQ, new Color(fstr), b.influence(BiomeInfluence.FOLIAGE));
        assertEquals(AM_NEQ, new Color(wstr), b.influence(BiomeInfluence.WATER));
        assertEquals(AM_NEQ, cmt, b.comment());
    }
}
