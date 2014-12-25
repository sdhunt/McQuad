/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit tests for ParsedArgs.
 *
 * @author Simon Hunt
 */
public class ParsedArgsTest extends AbstractTest {

    /*
 * NOTE: need to configure this test to run with a working directory
 *       that contains an "args" directory containing the following:
 *           <dir> test-output
 *           <dir> test-region
 *           <file> not-a-dir-1.txt
 *           <file> not-a-dir-2.txt
 */
    private static final String ARGS_DIR = "args/";
    private static final String REG_DIR = "test-region";
    private static final String OUT_DIR = "test-output";
    private static final String NEW_OUT_DIR = "new-output";
    private static final String NADIR_1 = "not-a-dir-1.txt";
    private static final String NADIR_2 = "not-a-dir-2.txt";
    private static final String DASH_O = " -o ";

    private ParsedArgs pa;

    private void parse(String args) {
        pa = ParsedArgs.build(args.split("\\s+"));
        print("parsed: {}", pa);
    }

    @Before
    public void setUp() {
        deleteNewOutDir();
    }

    @After
    public void tearDown() {
        deleteNewOutDir();
    }

    private void deleteNewOutDir() {
        File newOut = new File(ARGS_DIR + NEW_OUT_DIR);
        if (newOut.exists()) {
            boolean deleted = newOut.delete();
        }
    }

    @Test
    public void helpQuery() {
        title("helpQuery");
        parse("-?");
        assertNull(AM_HUH, pa.regionDir);
        assertNull(AM_HUH, pa.outputDir);
        assertFalse(AM_HUH, pa.valid());

        assertTrue(AM_HUH, pa.showHelp());
    }

    @Test
    public void helpWord() {
        title("helpWord");
        parse("-help");
        assertNull(AM_HUH, pa.regionDir);
        assertNull(AM_HUH, pa.outputDir);
        assertFalse(AM_HUH, pa.valid());

        assertTrue(AM_HUH, pa.showHelp());
    }

    @Test
    public void badRegionOnly() {
        title("badRegionOnly");
        parse("someRegion");
        assertEquals(AM_NEQ, "someRegion", pa.regionDir.getName());
        assertNull(AM_HUH, pa.outputDir);
        assertFalse(AM_HUH, pa.valid());

        assertFalse(AM_HUH, pa.showHelp());
    }

    @Test
    public void badRegionAndOutput() {
        title("badRegionAndOutput");
        parse("someRegion -o someOutput");
        assertEquals(AM_NEQ, "someRegion", pa.regionDir.getName());
        assertEquals(AM_NEQ, "someOutput", pa.outputDir.getName());
        assertFalse(AM_HUH, pa.valid());

        assertFalse(AM_HUH, pa.showHelp());
    }

    @Test
    public void outputDirIsRegionDir() {
        title("outputDirIsRegionDir");
        parse(ARGS_DIR + REG_DIR + DASH_O + ARGS_DIR + REG_DIR);
        assertEquals(AM_NEQ, REG_DIR, pa.regionDir.getName());
        assertEquals(AM_NEQ, REG_DIR, pa.outputDir.getName());
        assertFalse(AM_HUH, pa.valid());
    }

    @Test
    public void regionAndExistingOutput() {
        title("regionAndExistingOutput");
        parse(ARGS_DIR + REG_DIR + DASH_O + ARGS_DIR + OUT_DIR);
        assertEquals(AM_NEQ, REG_DIR, pa.regionDir.getName());
        assertEquals(AM_NEQ, OUT_DIR, pa.outputDir.getName());
        assertTrue(AM_HUH, pa.valid());
    }

    // FIXME
    @Test
    public void regionAndNewOutput() {
        title("regionAndNewOutput");
        parse(ARGS_DIR + REG_DIR + DASH_O + ARGS_DIR + NEW_OUT_DIR);
        assertEquals(AM_NEQ, REG_DIR, pa.regionDir.getName());
        assertEquals(AM_NEQ, NEW_OUT_DIR, pa.outputDir.getName());
        assertTrue(AM_HUH, pa.valid());
    }

    @Test
    public void nonDirRegion() {
        title("nonDirRegion");
        parse(ARGS_DIR + NADIR_1 + DASH_O + ARGS_DIR + OUT_DIR);
        assertEquals(AM_NEQ, NADIR_1, pa.regionDir.getName());
        assertEquals(AM_NEQ, OUT_DIR, pa.outputDir.getName());
        assertFalse(AM_HUH, pa.valid());
    }

    @Test
    public void nonDirOutput() {
        title("nonDirOutput");
        parse(ARGS_DIR + REG_DIR + DASH_O + ARGS_DIR + NADIR_1);
        assertEquals(AM_NEQ, REG_DIR, pa.regionDir.getName());
        assertEquals(AM_NEQ, NADIR_1, pa.outputDir.getName());
        assertFalse(AM_HUH, pa.valid());
    }

    @Test
    public void neitherRegionNorOutputIsDir() {
        title("neitherRegionNorOutputIsDir");
        parse(ARGS_DIR + NADIR_1 + DASH_O + ARGS_DIR + NADIR_2);
        assertEquals(AM_NEQ, NADIR_1, pa.regionDir.getName());
        assertEquals(AM_NEQ, NADIR_2, pa.outputDir.getName());
        assertFalse(AM_HUH, pa.valid());
    }

}
