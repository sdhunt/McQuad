/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link com.meowster.mcquad.BlockId}.
 *
 * @author Simon Hunt
 */
public class BlockIdTest extends AbstractTest {

    private BlockId id;
    private BlockId id2;

    @Test
    public void basic() {
        title("basic");
        id = new BlockId(0, 0);
        print(id);
        assertEquals(AM_NEQ, 0, id.id());
        assertEquals(AM_NEQ, 0, id.dv());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negId() {
        new BlockId(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void idTooLarge() {
        new BlockId(4096, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negDv() {
        new BlockId(0, -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dvTooLarge() {
        new BlockId(0, 16);
    }

    @Test
    public void equivalence() {
        title("equivalence");
        id = new BlockId(5, 2);
        print(id);
        BlockId other = new BlockId(5, 2);
        verifyEquals(id, other);
        assertEquals(AM_NEQ, 5, id.id());
        assertEquals(AM_NEQ, 2, id.dv());
    }

    @Test
    public void comparableSameIdsSameDvs() {
        title("comparableSameIdsSameDvs");
        id = new BlockId(0, 0);
        id2 = new BlockId(0, 0);
        assertTrue(AM_HUH, id.compareTo(id2) == 0);
        assertTrue(AM_HUH, id2.compareTo(id) == 0);
    }

    @Test
    public void comparableSameIdsDiffDvs() {
        title("comparableSameIdsDiffDvs");
        id = new BlockId(0, 0);
        id2 = new BlockId(0, 1);
        assertTrue(AM_HUH, id.compareTo(id2) < 0);
        assertTrue(AM_HUH, id2.compareTo(id) > 0);
    }

    @Test
    public void comparableDiffIdsSameDvs() {
        title("comparableDiffIdsSameDvs");
        id = new BlockId(1, 3);
        id2 = new BlockId(2, 3);
        assertTrue(AM_HUH, id.compareTo(id2) < 0);
        assertTrue(AM_HUH, id2.compareTo(id) > 0);
    }


    @Test
    public void comparableDiffIdsDiffDvs() {
        title("comparableDiffIdsDiffDvs");
        id = new BlockId(1, 7);
        id2 = new BlockId(2, 4);
        assertTrue(AM_HUH, id.compareTo(id2) < 0);
        assertTrue(AM_HUH, id2.compareTo(id) > 0);
    }

    @Test
    public void toStringDvNa() {
        title("toStringDvNa");
        id = new BlockId(3, -1);
        print(id);
        assertEquals(AM_NEQ, "BlockId{0x3:-}", id.toString());
    }

    @Test
    public void toStringDv15() {
        title("toStringDv15");
        id = new BlockId(3, 15);
        print(id);
        assertEquals(AM_NEQ, "BlockId{0x3:15}", id.toString());
    }
}
