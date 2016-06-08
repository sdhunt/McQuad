/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Unit tests for {@link com.meowster.mcquad.Coord}.
 *
 * @author Simon Hunt
 */
public class CoordTest extends AbstractTest {
    private Coord c;
    private Coord c2;

    @Test
    public void basic() {
        title("basic");
        c = new Coord(0, 0);
        print(c);
        assertEquals(AM_NEQ, 0, c.x());
        assertEquals(AM_NEQ, 0, c.z());
    }

    @Test
    public void same() {
        title("same");
        c = new Coord(5, 7);
        c2 = new Coord(5, 7);
        assertNotSame(AM_HUH, c, c2);
        assertEquals(AM_NEQ, c, c2);
    }

    @Test
    public void diff() {
        title("diff");
        c = new Coord(1, 23);
        c2 = new Coord(12, 3);
        assertNotSame(AM_HUH, c, c2);
        assertNotEquals(AM_NEQ, c, c2);
    }

    @Test
    public void negation() {
        title("negation");
        c = new Coord(1, 3);
        c2 = c.negation();
        assertEquals(AM_NEQ, -1, c2.x());
        assertEquals(AM_NEQ, -3, c2.z());
    }

    @Test
    public void fromString() {
        title("fromString");
        c = Coord.coord("[1, 2]");
        print(c);
        assertEquals(AM_NEQ, 1, c.x());
        assertEquals(AM_NEQ, 2, c.z());
    }

    @Test
    public void fromStringNeg() {
        title("fromStringNeg");
        c = Coord.coord("[-3,-5]");
        print(c);
        assertEquals(AM_NEQ, -3, c.x());
        assertEquals(AM_NEQ, -5, c.z());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringBad() {
        title("fromStringBad");
        Coord.coord("bad");
    }
}
