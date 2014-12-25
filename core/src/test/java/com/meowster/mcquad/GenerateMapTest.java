/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;

/**
 * Base class for running map generation tests.
 *
 * @author Simon Hunt
 */
public abstract class GenerateMapTest extends AbstractTest {

    // FIXME
    private static final String SAVES_DIR =
            "/Users/simonh/AppData/Roaming/.minecraft/saves/";

    private static final String REGION = "/region";
    private static final String OUT_SLASH = "out/generatedMaps/";
    private static final String DASH_OUT = "-out";
    private static final String DASH_O = " -o ";

    protected String regionDirString(String worldName) {
        return SAVES_DIR + worldName + REGION;
    }

    protected String outputDirString(String worldName) {
        return OUT_SLASH + worldName + DASH_OUT;
    }

    protected ParsedArgs parse(String s) {
        return ParsedArgs.build(s.split("\\s+"));
    }

    protected String goodArgs(String worldName) {
        return regionDirString(worldName) + DASH_O + outputDirString(worldName);
    }
}
