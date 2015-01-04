/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link QuadLevelBuilderOld}.
 *
 * @author Simon Hunt
 */
public class QuadLevelBuilderTest extends AbstractTest {

    private QuadData quadData;
    private QuadLevelBuilderOld builder;
    private QuadLevel level;

    private static final int[][] FAKE_REGION_1 = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1},
            {2, -1},
            {2, 0},
            {3, 0},
            {4, 0},
    };

    private Set<Region> makeRegions(int[][] coords) {
        Set<Region> regions = new HashSet<>();
        for (int[] c : coords)
            regions.add(new Region(c[0], c[1]));
        return regions;
    }

    private RegionData createRegionData() {
        RegionData rd = new RegionData();
        rd.load(makeRegions(FAKE_REGION_1));
        return rd;
    }

    private QuadData createQuadData() {
        return new QuadData(createRegionData());
    }

    @Before
    public void setUp() {
        quadData = createQuadData();
        builder = new QuadLevelBuilderOld(quadData);
        level = builder.prepare();
        builder.genTiles();
    }


    @Test @Ignore("until we fix the builders")
    public void basic() {
        title("basic");

        // zoom level 3 built in the @Before method, for us
        print("\nbuilt: {}", level);
        print(level.schematic());

        assertEquals(AM_UXS, 3, level.zoom());
        assertEquals(AM_NEQ, "z3", level.name());
        assertEquals(AM_UXS, 512, level.blocksPerTile());
        assertEquals(AM_NEQ, new Coord(1, 3), level.originTile());
        assertEquals(AM_NEQ, new Coord(0, 0), level.originDisplace());

        Collection<QuadTile> tiles = level.tiles();
        assertEquals(AM_UXS, 8, tiles.size());
        for (QuadTile qt: tiles) {
            Coord c = qt.coord();
            Region r = quadData.at(c.x(), c.z());
            print("{} => {}", c, r);
            assertNotNull("missing region", r);
        }
    }

    @Test @Ignore("until we fix the builders")
    public void buildZoom2And1() {
        title("buildZoom2And1");
        print("built: {}", level);
        print(level.schematic());

        // Build zoom level 2
        builder = new QuadLevelBuilderOld(level);
        level = builder.prepare();
        builder.genTiles(true);
        print("\nbuilt: {}", level);
        print(level.schematic());

        assertEquals(AM_UXS, 2, level.zoom());
        assertEquals(AM_NEQ, "z2", level.name());
        assertEquals(AM_UXS, 1024, level.blocksPerTile());
        assertEquals(AM_NEQ, new Coord(0, 1), level.originTile());
        assertEquals(AM_NEQ, new Coord(512, 512), level.originDisplace());

        Collection<QuadTile> tiles = level.tiles();
        assertEquals(AM_UXS, 5, tiles.size());
        for (QuadTile t: tiles)
            print(t);


        // Build zoom level 1
        builder = new QuadLevelBuilderOld(level);
        level = builder.prepare();
        builder.genTiles(true);
        print("\nbuilt: {}", level);
        print(level.schematic());

        assertEquals(AM_UXS, 1, level.zoom());
        assertEquals(AM_NEQ, "z1", level.name());
        assertEquals(AM_UXS, 2048, level.blocksPerTile());
        assertEquals(AM_NEQ, new Coord(0, 0), level.originTile());
        assertEquals(AM_NEQ, new Coord(512, 512 + 1024), level.originDisplace());

        tiles = level.tiles();
        assertEquals(AM_UXS, 3, tiles.size());
        for (QuadTile t: tiles)
            print(t);
    }
}
