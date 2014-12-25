/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link com.meowster.mcquad.McQuad}.
 *
 * @author Simon Hunt
 */
public class McQuadBadArgsTest extends GenerateMapTest {

    private static final String NADIR_1 = "not-a-dir-1.txt";

    McQuad mq;
    int result;

    @Test
    public void noArgs() {
        title("noArgs");
        mq = new McQuad(parse(""));
        print("{}", mq.parsedArgs());
        result = mq.process();
        assertEquals(AM_NEQ, 0, result);
        assertFalse(AM_HUH, mq.processingDone());
    }

    @Test
    public void badArgs() {
        title("badArgs");
        mq = new McQuad(parse(NADIR_1));
        print("{}", mq.parsedArgs());
        result = mq.process();
        assertEquals(AM_NEQ, 1, result);
        assertFalse(AM_HUH, mq.processingDone());
    }

}
