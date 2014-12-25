/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.rec;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link FileRecordStore}.
 *
 * @author Simon Hunt
 */
public class FileRecordStoreTest extends AbstractTest {

    private static final String SOME_FILE = "com/meowster/mcquad/some-file.txt";
    private static final String NO_SUCH_FILE = "hey/there/dude";

    private FileRecordStore store;

    @Test(expected = NullPointerException.class)
    public void nullParam() {
        new FileRecordStore(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSuchFile() {
        new FileRecordStore(NO_SUCH_FILE);
    }

    @Test
    public void someFile() {
        title("someFile");
        store = new FileRecordStore(SOME_FILE);
        print(store.toDebugString());
        assertEquals(AM_UXS, 2, store.size());
        Iterator<Record> iter = store.getRecords().iterator();
        assertEquals(AM_NEQ, "Some data = foo", iter.next().raw());
        assertEquals(AM_NEQ, "123", iter.next().raw());
    }

    @Test
    public void someFileWithBlanks() {
        title("someFileWithBlanks");
        store = new FileRecordStore(SOME_FILE, false);
        print(store.toDebugString());
        assertEquals(AM_UXS, 3, store.size());
        Iterator<Record> iter = store.getRecords().iterator();
        assertEquals(AM_NEQ, "", iter.next().raw());
        assertEquals(AM_NEQ, "Some data = foo", iter.next().raw());
        assertEquals(AM_NEQ, "123", iter.next().raw());
    }
}
