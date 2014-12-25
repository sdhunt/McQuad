/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.rec;

import java.util.List;

/**
 * Defines a store of records.
 *
 * @author Simon Hunt
 */
public interface RecordStore {

    /**
     * Returns the records in the store.
     *
     * @return the records
     */
    List<Record> getRecords();

    /**
     * Returns the number of records in the store.
     *
     * @return the size of the store
     */
    int size();
}
