/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.rec;

/**
 * A base implementation of a record.
 *
 * @author Simon Hunt
 */
public class DefaultRecord implements Record {

    private String raw;

    /**
     * Constructs a record based on the specified raw data.
     *
     * @param raw the raw data
     */
    public DefaultRecord(String raw) {
        this.raw = raw;
        parseRaw(raw);
    }

    /**
     * Parses the raw data. This method is called from the constructor.
     * <p>
     * This default implementation does nothing extra. This method can be
     * overridden by subclasses to convert the raw data into a form more
     * suitable for runtime queries.
     *
     * @param raw the raw data
     */
    protected void parseRaw(String raw) {
    }

    @Override
    public String raw() {
        return raw;
    }

    @Override
    public String toString() {
        return "DefaultRecord{raw='" + raw + '\'' + '}';
    }
}
