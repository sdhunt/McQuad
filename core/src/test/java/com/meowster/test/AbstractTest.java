/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.test;

import com.meowster.util.StringUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Superclass to all unit tests.
 *
 * @author Simon Hunt
 */
public abstract class AbstractTest {

    // Assertion Messages
    public static final String AM_HUH = "Huh?";
    public static final String AM_NEQ = "Not equivalent";
    public static final String AM_UXS = "Unexpected size";
    public static final String AM_WRCL = "Wrong class";


    /**
     * Prints the given items to STDOUT.
     *
     * @param items the items to be printed
     */
    public void print(Object... items) {
        StringBuilder sb = new StringBuilder();
        for (Object item : items)
            sb.append(item).append(" ");
        StringUtils.printOut(sb);
    }

    /**
     * Prints the given format / items to STDOUT.
     *
     * @param fmt the format string
     * @param items the items to be formatted
     */
    public void print(String fmt, Object... items) {
        StringUtils.printOut(fmt, items);
    }

    /**
     * Prints a title for the test.
     *
     * @param methodName the method name
     */
    public void title(String methodName) {
        print("\n{}()", methodName);
    }

    /**
     * Compares two objects and verifies that they are equal.
     *
     * @param a object a
     * @param b object b
     */
    public void verifyEquals(Object a, Object b) {
        assertEquals("a != b", a, b);
        assertEquals("b != a", b, a);
        assertEquals("hashcodes differ", a.hashCode(), b.hashCode());
    }
}
