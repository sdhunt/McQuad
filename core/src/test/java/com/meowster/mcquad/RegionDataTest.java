/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link com.meowster.mcquad.RegionData}.
 *
 * @author Simon Hunt
 */
public class RegionDataTest extends AbstractMcQuadTest {

    private RegionData rd;

    private void checkBounds(int xMin, int xMax, int zMin, int zMax) {
        Bounds b = rd.bounds();
        assertEquals(AM_NEQ, xMin, b.minX());
        assertEquals(AM_NEQ, xMax, b.maxX());
        assertEquals(AM_NEQ, zMin, b.minZ());
        assertEquals(AM_NEQ, zMax, b.maxZ());
    }

    private void checkWhd(int w, int h, int d) {
        Bounds b = rd.bounds();
        assertEquals(AM_NEQ, w, b.nx());
        assertEquals(AM_NEQ, h, b.nz());
        assertEquals(AM_NEQ, d, b.maxDim());
    }

    @Before
    public void setUp() {
        rd = new RegionData();
    }

    @Test
    public void basic() {
        print(rd);
        assertEquals(AM_UXS, 0, rd.size());
        checkBounds(0,0,0,0);
        checkWhd(0, 0, 0);
    }

    @Test
    public void regionA() {
        title("regionA");
        rd.loadMock(regionDir("a"));
        print(rd);
        assertEquals(AM_UXS, 4, rd.size());
        checkBounds(-1, 0, -1, 0);
        checkWhd(2, 2, 2);
    }

    @Test
    public void regionB() {
        title("regionB");
        rd.loadMock(regionDir("b"));
        print(rd);
        assertEquals(AM_UXS, 6, rd.size());
        checkBounds(-1, 2, -1, 1);
        checkWhd(4, 3, 4);
    }

    private Set<Region> makeRegions(int[][] coords) {
        Set<Region> regions = new HashSet<>();
        for (int[] c : coords)
            regions.add(new Region(c[0], c[1]));
        return regions;
    }

    private static final int[][] FAKE_REGION_1 = {
            {0, 0}, {1, 2}, {1, 1}
    };

    @Test
    public void fakeRegion1() {
        title("fakeRegion1");
        rd.load(makeRegions(FAKE_REGION_1));
        print(rd);
        assertEquals(AM_UXS, 3, rd.size());
        checkBounds(0, 1, 0, 2);
        checkWhd(2, 3, 3);

        checkAt(0, 0, true);
        checkAt(0, 1, false);
        checkAt(0, 2, false);
        checkAt(1, 0, false);
        checkAt(1, 1, true);
        checkAt(1, 2, true);
    }

    private void checkAt(int x, int z, boolean present) {
        Region r = rd.at(x, z);
        if (present) {
            assertNotNull("region missing", r);
            assertEquals(AM_NEQ, x, r.coord().x());
            assertEquals(AM_NEQ, z, r.coord().z());
        }
    }

}
