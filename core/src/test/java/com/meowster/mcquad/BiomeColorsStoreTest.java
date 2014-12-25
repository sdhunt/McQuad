/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;


import com.meowster.rec.Record;
import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link com.meowster.mcquad.BiomeColorsStore}.
 *
 * @author Simon Hunt
 */
public class BiomeColorsStoreTest extends AbstractTest {

    private static final String DEFAULT_REC_STR =
            "default 0xffff00ff  0xff00ffff  0xffffff00";
    private BiomeColorsStore store;

    @Before
    public void setUp() {
        store = new BiomeColorsStore();
    }

    @Test
    public void basic() {
        title("basic");
        print(store);
        assertEquals(AM_UXS, 60, store.size());
        Iterator<Record> iter = store.getRecords().iterator();
        Record rec = iter.next();
        assertTrue(AM_WRCL, rec instanceof BiomeColorsStore.BiomeRecord);
        assertEquals(AM_NEQ, DEFAULT_REC_STR, rec.raw());
    }

    @Test
    public void firstFewRecordsCheckStrings() {
        title("firstFewRecordsCheckStrings");
        Iterator<Record> iter = store.getRecords().iterator();
        BiomeColorsStore.BiomeRecord brec =
                (BiomeColorsStore.BiomeRecord) iter.next();
        assertNull(AM_HUH, brec.biomeId());
        assertEquals(AM_NEQ, Color.MAGENTA, brec.grass());
        assertEquals(AM_NEQ, Color.CYAN, brec.foliage());
        assertEquals(AM_NEQ, Color.YELLOW, brec.water());

        checkEntry(iter.next(), 0, "0xff8eb971", "0xff71a74d", "0xff0064e4");
        checkEntry(iter.next(), 1, "0xff91bd59", "0xff77ab2f", "0xff00a0d4");
    }

    private void checkEntry(Record record, int id, String grass,
                            String foliage, String water) {
        BiomeColorsStore.BiomeRecord brec =
                (BiomeColorsStore.BiomeRecord) record;
        print(brec);
        assertEquals(AM_NEQ, id, brec.biomeId().id());
        assertEquals(AM_NEQ, grass, brec.grass().hex());
        assertEquals(AM_NEQ, foliage, brec.foliage().hex());
        assertEquals(AM_NEQ, water, brec.water().hex());
    }
}
