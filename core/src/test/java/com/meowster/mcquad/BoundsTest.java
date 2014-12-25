/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.meowster.mcquad.Bounds}.
 *
 * @author Simon Hunt
 */
public class BoundsTest extends AbstractTest {

    private Bounds b;

    @Before
    public void setUp() {
        b = new Bounds();
    }

    private void minMaxCheck(int xMin, int xMax, int zMin, int zMax) {
        assertEquals(AM_NEQ, xMin, b.minX());
        assertEquals(AM_NEQ, xMax, b.maxX());
        assertEquals(AM_NEQ, zMin, b.minZ());
        assertEquals(AM_NEQ, zMax, b.maxZ());
    }

    private void whdCheck(int w, int h, int d) {
        assertEquals(AM_UXS, w, b.nx());
        assertEquals(AM_UXS, h, b.nz());
        assertEquals(AM_UXS, d, b.maxDim());
    }

    @Test
    public void basic() {
        title("basic");
        print(b);
        minMaxCheck(0, 0, 0, 0);
        whdCheck(0, 0, 0);
    }

    @Test
    public void singleUnit() {
        title("singleUnit");
        b.add(1, 1);
        print(b);
        minMaxCheck(1, 1, 1, 1);
        whdCheck(1, 1, 1);
    }

    @Test
    public void threeByFive() {
        title("threeByFive");
        b.add(-2, -1);
        b.add(2, 1);
        print(b);
        minMaxCheck(-2, 2, -1, 1);
        whdCheck(5, 3, 5);
    }

    @Test
    public void several() {
        title("several");
        b.add(-1,-3);
        print(b);
        minMaxCheck(-1, -1, -3, -3);
        whdCheck(1, 1, 1);

        b.add(0, 0);
        print(b);
        minMaxCheck(-1, 0, -3, 0);
        whdCheck(2, 4, 4);

        b.add(2, -2);
        print(b);
        minMaxCheck(-1, 2, -3, 0);
        whdCheck(4, 4, 4);
    }
}
