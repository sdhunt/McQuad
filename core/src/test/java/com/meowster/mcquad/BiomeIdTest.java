/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link com.meowster.mcquad.BiomeId}.
 *
 * @author Simon Hunt
 */
public class BiomeIdTest extends AbstractTest {

    private BiomeId id;
    private BiomeId id2;

    @Test
    public void basic() {
        title("basic");
        id = new BiomeId(0);
        print(id);
        assertEquals(AM_NEQ, 0, id.id());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negId() {
        new BiomeId(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void idTooLarge() {
        new BiomeId(256);
    }

    @Test
    public void equivalence() {
        title("equivalence");
        id = new BiomeId(5);
        print(id);
        BiomeId other = new BiomeId(5);
        verifyEquals(id, other);
        assertEquals(AM_NEQ, 5, id.id());
    }

    @Test
    public void comparableSameIds() {
        title("comparableSameIds");
        id = new BiomeId(3);
        id2 = new BiomeId(3);
        assertTrue(AM_HUH, id.compareTo(id2) == 0);
        assertTrue(AM_HUH, id2.compareTo(id) == 0);
    }

    @Test
    public void comparableDiffIds() {
        title("comparableDiffIds");
        id = new BiomeId(1);
        id2 = new BiomeId(2);
        assertTrue(AM_HUH, id.compareTo(id2) < 0);
        assertTrue(AM_HUH, id2.compareTo(id) > 0);
    }

}
