/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.meowster.mcquad.QuadLevelBuilderFactory}.
 *
 * @author Simon Hunt
 */
public class QuadLevelBuilderFactoryTest extends AbstractTest {

    private static final File OUTPUT_DIR = new File("foo");
    private static final int[][] FAKE_REGIONS = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1},
            {2, -1},
            {2, 0},
            {3, 0},
            {4, 0},
    };

    private QuadLevelBuilderFactory factory;
    private QuadLevelBuilder builder;


    private Set<Region> makeRegions(int[][] coords) {
        Set<Region> regions = new HashSet<>();
        for (int[] c : coords)
            regions.add(new Region(c[0], c[1]));
        return regions;
    }

    private RegionData createRegionData() {
        RegionData rd = new RegionData();
        rd.load(makeRegions(FAKE_REGIONS));
        return rd;
    }

    private QuadData createQuadData() {
        return new QuadData(createRegionData());
    }

    private QuadLevel createQuadLevel() {
        return new QdLvl();
    }

    @Before
    public void setUp() {
        factory = new QuadLevelBuilderFactory(OUTPUT_DIR);
    }

    @Test
    public void quadDataBuilder() {
        title("quadDataBuilder");
        builder = factory.createBuilder(createQuadData());
        assertEquals(AM_WRCL, BaseQuadLevelBuilder.class, builder.getClass());
    }

    @Test
    public void levelBuilder() {
        title("levelBuilder");
        builder = factory.createBuilder(createQuadLevel());
        assertEquals(AM_WRCL, ZoomQuadLevelBuilder.class, builder.getClass());
    }
}
