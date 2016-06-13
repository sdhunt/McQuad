/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.Record;
import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link com.meowster.mcquad.BlockColorsStore}.
 *
 * @author Simon Hunt
 */
public class BlockColorsStoreTest extends AbstractTest {

    private static final int EXP_NUM_BLOCK_RECORDS = 482;

    private BlockColorsStore store;

    @Before
    public void setUp() {
        store = new BlockColorsStore();
    }

    @Test
    public void basic() {
        title("basic");
        print(store);
        assertEquals(AM_UXS, EXP_NUM_BLOCK_RECORDS, store.size());
    }

    @Test
    public void firstFewRecordsCheckInts() {
        title("firstFewRecordsCheckInts");
        Iterator<Record> iter = store.getRecords().iterator();
        checkIdDv(iter.next(), -1);
        checkIdDv(iter.next(), 0);
        checkIdDv(iter.next(), 1, 0);
        checkIdDv(iter.next(), 1, 1);
        checkIdDv(iter.next(), 1, 2);
        checkIdDv(iter.next(), 1, 3);
        checkIdDv(iter.next(), 1, 4);
        checkIdDv(iter.next(), 1, 5);
        checkIdDv(iter.next(), 1, 6);
        checkIdDv(iter.next(), 2);
        checkIdDv(iter.next(), 3, 0);
        checkIdDv(iter.next(), 3, 1);
        checkIdDv(iter.next(), 3, 2);
        checkIdDv(iter.next(), 4);
        checkIdDv(iter.next(), 5, 0);
        checkIdDv(iter.next(), 5, 1);
        checkIdDv(iter.next(), 5, 2);
        checkIdDv(iter.next(), 5, 3);
        checkIdDv(iter.next(), 5, 4);
        checkIdDv(iter.next(), 5, 5);
    }

    private void checkIdDv(Record rec, int id) {
        checkIdDv(rec, id, BlockId.DV_NA);
    }

    private void checkIdDv(Record rec, int id, int dv) {
        BlockColorsStore.BlockRecord brec = (BlockColorsStore.BlockRecord) rec;
        print(brec);
        if (id == -1)
            assertNull(AM_HUH, brec.blockId());
        else {
            BlockId blockId = brec.blockId();
            assertEquals(AM_NEQ, id, blockId.id());
            assertEquals(AM_NEQ, dv, blockId.dv());
        }
    }
}
