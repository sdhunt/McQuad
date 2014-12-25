/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import com.meowster.util.ImageUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link com.meowster.mcquad.TileUtils}.
 *
 * @author Simon Hunt
 */
public class TileUtilsTest extends AbstractTest {
    private static final File REGION_DIR =
            new File("/Users/simonh/AppData/Roaming/.minecraft/saves/MyWorld/region");
    private static final File REGION_ZERO_ZERO =
            new File(REGION_DIR, "r.0.0.mca");
    private static final File IMAGE_OUTDIR = new File("image-out");

    private TileUtils utils;

    @BeforeClass
    public static void classSetUp() {
        if (!IMAGE_OUTDIR.exists())
            if (!IMAGE_OUTDIR.mkdir())
                fail("Failed to create image output directory: " + IMAGE_OUTDIR);
    }

    @Before
    public void setUp() {
        utils = new TileUtils();
    }

    @Test
    public void regionZeroZero() {
        title("regionZeroZero");
        Region r = new Region(0, 0, REGION_ZERO_ZERO, false);
        print(r);
        assertEquals(AM_NEQ, "t.0.0.png", r.tileName());

        BufferedImage bi = TileUtils.generateImage(r);
        assertNotNull(AM_HUH, bi);
        File imageFile = new File(IMAGE_OUTDIR, r.tileName());
        ImageUtils.writeImageToDisk(bi, imageFile);
        printDefaultedReport();
    }

    private void printDefaultedReport() {
        print("Defaulted Block IDs:");
        printSet(BlockColors.BLOCK_DB.getDefaulted());
        print("Defaulted Biome IDs:");
        printSet(BiomeColors.BIOME_DB.getDefaulted());
    }

    private void printSet(Set<?> defaulted) {
        if (defaulted.isEmpty()) {
            print("  (none)");
        } else {
            for (Object o: defaulted)
                print("  {}", o);
        }
    }

}
