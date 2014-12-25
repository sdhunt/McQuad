/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link com.meowster.mcquad.QuadData}.
 *
 * @author Simon Hunt
 */
public class QuadDataTest extends AbstractMcQuadTest {

    private QuadData qd;

    private RegionData makeRegionData(int[][] rawCoords) {
        RegionData rd = new RegionData();
        rd.load(makeRegions(rawCoords));
        return rd;
    }

    private Set<Region> makeRegions(int[][] rawCoords) {
        Set<Region> regions = new HashSet<>();
        for (int[] c: rawCoords)
            regions.add(new Region(c[0], c[1]));
        return regions;
    }

    private Coord xz(int x, int z) {
        return new Coord(x, z);
    }

    private void printQd() {
        print(qd);
        print(qd.regionData());
        print(qd.schematic());
    }

    private void checkRow(int rnum, Coord... expRegs) {
        for (int x=0; x<expRegs.length; x++) {
            Region r = qd.at(x, rnum);
            Coord c = expRegs[x];
            if (c == null)
                assertNull("Unexpected region " + r, r);
            else if (r == null)
                fail("Region" + c + " not found at q[" + x + "," + rnum + "]");
            else
                assertEquals("Unexpected coords " + r.coord(), c, r.coord());
        }
    }


    private static final int[][] REGION_1 = {
            {0, 0}, {0, -1}
    };

    @Test
    public void basic() {
        title("basic");
        qd = new QuadData(makeRegionData(REGION_1));
        printQd();

        assertEquals(AM_NEQ, 1, qd.maxZoom());
        checkRow(0, xz(0, -1), null);
        checkRow(1, xz(0, 0), null);
    }

    private static final int[][] REGION_2 = {
            {-1, -1}, {-1, 0}, {0, 0}, {0, 1}
    };

    @Test
    public void dogleg() {
        title("dogleg");
        qd = new QuadData(makeRegionData(REGION_2));
        printQd();

        assertEquals(AM_NEQ, 2, qd.maxZoom());
        checkRow(0, null, xz(-1,-1), null, null);
        checkRow(1, null, xz(-1,0), xz(0,0), null);
        checkRow(2, null, null, xz(0,1), null);
        checkRow(3, null, null, null, null);
    }

    private static final int[][] REGION_3 = {
            {-3, -3}, {-3, -2}, {-2, -2}, {-2, -1}, {-1, -1}, {-1, 0},
            {0, 0}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {1, -2}, {2, -2}

    };

    @Test
    public void snake() {
        title("snake");
        qd = new QuadData(makeRegionData(REGION_3));
        printQd();

        assertEquals(AM_NEQ, 3, qd.maxZoom());
        checkRow(0, null, null, null, null, null, null, null, null);
        checkRow(1, null, xz(-3,-3), null, null, null, null, null, null);
        checkRow(2, null, xz(-3,-2), xz(-2,-2), null, null, xz(1,-2), xz(2,-2), null);
        checkRow(3, null, null, xz(-2,-1), xz(-1,-1), null, xz(1,-1), null, null);
        checkRow(4, null, null, null, xz(-1,0), xz(0,0), xz(1,0), null, null);
        checkRow(5, null, null, null, null, xz(0,1), xz(1,1), null, null);
        checkRow(6, null, null, null, null, null, null, null, null);
        checkRow(7, null, null, null, null, null, null, null, null);
    }

    private static final int[][] REGION_4 = {
            {-1, 0}, {0, 0}, {1, 0}, {2, 0}, {3, 0}
    };

    @Test
    public void line() {
        title("line");
        qd = new QuadData(makeRegionData(REGION_4));
        printQd();

        assertEquals(AM_NEQ, 3, qd.maxZoom());
        checkRow(0, null, null, null, null, null, null, null, null);
        checkRow(1, null, null, null, null, null, null, null, null);
        checkRow(2, null, null, null, null, null, null, null, null);
        checkRow(3, null, xz(-1,0), xz(0,0), xz(1,0), xz(2,0), xz(3,0), null, null);
        checkRow(4, null, null, null, null, null, null, null, null);
        checkRow(5, null, null, null, null, null, null, null, null);
        checkRow(6, null, null, null, null, null, null, null, null);
        checkRow(7, null, null, null, null, null, null, null, null);
    }

    @Test
    public void regionDirA() {
        title("regionDirA");
        RegionData rd = new RegionData();
        rd.loadMock(regionDir("a"));
        qd = new QuadData(rd);
        printQd();

        assertEquals(AM_NEQ, 1, qd.maxZoom());
        checkRow(0, xz(-1,-1), xz(0,-1));
        checkRow(1, xz(-1,0), xz(0,0));
    }

    @Test
    public void regionDirB() {
        title("regionDirB");
        RegionData rd = new RegionData();
        rd.loadMock(regionDir("b"));
        qd = new QuadData(rd);
        printQd();

        assertEquals(AM_NEQ, 2, qd.maxZoom());
        checkRow(0, xz(-1, -1), null, null, null);
        checkRow(1, xz(-1, 0), xz(0, 0), xz(1, 0), xz(2, 0));
        checkRow(2, null, null, null, xz(2, 1));
        checkRow(3, null, null, null, null);
    }

}
