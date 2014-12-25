/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link com.meowster.mcquad.RegionFile}.
 *
 * @author Simon Hunt
 */
public class RegionFileTest extends AbstractTest {
    private static final String R00 = "r00";
    private static final String R00MCA = "r.0.0.mca";
    private static final File REGION_ZERO = new File(R00, R00MCA);

    @Test @Ignore("Until we can figure out the relative path issue")
    public void basic() {
        assertTrue("test region file does not exist", REGION_ZERO.exists());

        RegionFile rf = new RegionFile(REGION_ZERO, false);
        print(rf);
        assertEquals(AM_UXS, 586, rf.getSectorCount());
    }
}
