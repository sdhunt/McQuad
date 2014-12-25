/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Generate map for McQuad01 world.
 *
 * @author Simon Hunt
 */
public class GenMcQuadTest extends GenerateMapTest {
    // FIXME
    private void gen(String worldName) {
        McQuad mq = new McQuad(parse(goodArgs(worldName)));
        print("{}", mq.parsedArgs());
        mq.process();
    }

    @Test @Ignore
    public void gen01() {
        title("gen01");
        gen("McQuad01");
    }

    @Test @Ignore
    public void gen02() {
        title("gen02");
        gen("McQuad02");
    }

    @Test @Ignore
    public void gen03() {
        title("gen03");
        gen("McQuad03");
    }

    @Test @Ignore
    public void gen04() {
        title("gen04");
        gen("McQuad04");
    }

    @Test @Ignore
    public void genMyWorld() {
        title("genMyWorld");
        gen("MyWorld");
    }
}
