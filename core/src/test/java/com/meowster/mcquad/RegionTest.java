/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link com.meowster.mcquad.Region}.
 *
 * @author Simon Hunt
 */
public class RegionTest extends AbstractTest {

    private static final File FOO_FILE = new File("foo");
    private static final File BAR_FILE = new File("bar");

    Region r;
    Region r2;

    @Test
    public void basic() {
        title("basic");
        r = new Region(2, 3);
        print(r);

        assertNull(AM_HUH, r.regionFile());
        assertEquals(AM_NEQ, 2, r.coord().x());
        assertEquals(AM_NEQ, 3, r.coord().z());
        assertEquals(AM_NEQ, new Coord(2,3), r.coord());
    }

    @Test
    public void equalityDoesntUseFile() {
        title("equalityDoesntUseFile");
        r = new Region(2, 3, FOO_FILE, true);
        r2 = new Region(2, 3, BAR_FILE, true);
        print(r, r2);
        assertNotSame(AM_HUH, r, r2);
        assertEquals(AM_NEQ, r, r2);
    }

    @Test
    public void differ() {
        title("differ");
        r = new Region(1, 1, FOO_FILE, true);
        r2 = new Region(1, 2, FOO_FILE, true);
        print(r, r2);
        assertNotSame(AM_HUH, r, r2);
        assertNotEquals(AM_HUH, r, r2);
    }
}
