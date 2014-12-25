/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;

import java.io.File;

/**
 * Base class for McQuad unit tests.
 *
 * @author Simon Hunt
 */
public abstract class AbstractMcQuadTest extends AbstractTest {

    static final String REGIONS_PATH = "regions/";

    /**
     * Returns a test region directory, relative to "{@value #REGIONS_PATH}".
     *
     * @param name test region name
     * @return corresponding test directory.
     */
    File regionDir(String name) {
        return new File(REGIONS_PATH, name);
    }

}
