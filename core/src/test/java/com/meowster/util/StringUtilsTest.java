/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.util;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link com.meowster.util.PathUtils}.
 *
 * @author Simon Hunt
 */
public class StringUtilsTest extends AbstractTest {
    private static final ClassLoader CL = StringUtilsTest.class.getClassLoader();
    private static final String FILE_LOCATION =
            "/Users/simonh/dev/mcquad/McQuad/core/src/test/resources";
    private static final String SOME_FILE = "com/meowster/mcquad/some-file.txt";
    private static final String LINE = "}-------------{";

    private String fmt(String fmt, Object... items) {
        return StringUtils.format(fmt, items);
    }

    @Test
    public void format0() {
        assertEquals(AM_NEQ, "", fmt(""));
    }

    @Test
    public void format1() {
        assertEquals(AM_NEQ, "Foo Bar", fmt("{} {}", "Foo", "Bar"));
    }

    @Test
    public void format2() {
        assertEquals(AM_NEQ, "--xxx--", fmt("--{}--", "xxx"));
    }

    @Test
    public void format3() {
        assertEquals(AM_NEQ, "25ms", fmt("{}ms", 25));
    }

    @Test
    public void formatNull() {
        assertEquals(AM_NEQ, "{null}", fmt("{}", new Object[]{null}));
    }

    @Test
    public void getFileContentsOne() throws IOException {
        title("getFileContentsOne");
        String s = StringUtils.getFileContents(SOME_FILE, CL);
        assertNotNull("no file read", s);
        print(LINE);
        print("{}", s);
        print(LINE);
        // TODO: assert file contents
    }

    @Test
    public void stripComments() throws IOException {
        title("stripComments");
        String s = StringUtils.getFileContents(SOME_FILE, CL);
        String stripped = StringUtils.stripCommentLines(s);
        print(LINE);
        print(stripped);
        print(LINE);
        // TODO: assert stripped contents
    }

    @Test
    public void stringAsLines() throws IOException {
        title("stringAsLines");
        String s = StringUtils.getFileContents(SOME_FILE, CL);
        List<String> contents = StringUtils.stringAsLines(s);
        print(contents);
        assertEquals(AM_NEQ, 3, contents.size());
        assertEquals(AM_NEQ, "Some data = foo", contents.get(0));
        assertEquals(AM_NEQ, "123", contents.get(1));
        assertEquals(AM_NEQ, "Another line", contents.get(2));
    }

    @Test
    public void linesFromFile() throws IOException {
        File f = new File(FILE_LOCATION, SOME_FILE);
        List<String> strings = StringUtils.getFileContentsAsList(f, false);
        print(strings);
        assertEquals(AM_NEQ, 7, strings.size());
        assertEquals(AM_NEQ, "### A test file", strings.get(0));
        assertEquals(AM_NEQ, "123", strings.get(3));
        assertEquals(AM_NEQ, "", strings.get(5));
    }

    @Test
    public void linesFromFileStripped() throws IOException {
        File f = new File(FILE_LOCATION, SOME_FILE);
        List<String> strings = StringUtils.getFileContentsAsList(f, true);
        print(strings);
        assertEquals(AM_NEQ, 3, strings.size());
        assertEquals(AM_NEQ, "Some data = foo", strings.get(0));
        assertEquals(AM_NEQ, "123", strings.get(1));
    }

    @Test
    public void hexAsLongOne() {
        title("hexAsLongOne");
        assertEquals(AM_NEQ, 0x555L, StringUtils.hexAsLong("555"));
        assertEquals(AM_NEQ, 0x555L, StringUtils.hexAsLong("0555"));
        assertEquals(AM_NEQ, 0x555L, StringUtils.hexAsLong("555"));
        assertEquals(AM_NEQ, 0x555L, StringUtils.hexAsLong("0x0555"));
    }

    @Test
    public void hexAsLongTwo() {
        title("hexAsLongOne");
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("a"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("0a"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("A"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("0A"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("0x0A"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("0x0a"));
        assertEquals(AM_NEQ, 10L, StringUtils.hexAsLong("0x0000000a"));
    }

    @Test
    public void hexOne() {
        title("hexOne");
        assertEquals(AM_NEQ, 0x555, StringUtils.hex("555"));
        assertEquals(AM_NEQ, 0x555, StringUtils.hex("0555"));
        assertEquals(AM_NEQ, 0x555, StringUtils.hex("555"));
        assertEquals(AM_NEQ, 0x555, StringUtils.hex("0x0555"));
    }

    @Test
    public void hexTwo() {
        title("hexOne");
        assertEquals(AM_NEQ, 12, StringUtils.hex("c"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("0c"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("C"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("0C"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("0x0C"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("0x0c"));
        assertEquals(AM_NEQ, 12, StringUtils.hex("0x0000000c"));
    }

    @Test
    public void asHexLong() {
        title("asHexLong");
        assertEquals(AM_NEQ, "0x0", StringUtils.asHex(0L));
        assertEquals(AM_NEQ, "0xa", StringUtils.asHex(10L));
        assertEquals(AM_NEQ, "0x64", StringUtils.asHex(100L));
    }

    @Test
    public void asHexInt() {
        title("asHexInt");
        assertEquals(AM_NEQ, "0x0", StringUtils.asHex(0));
        assertEquals(AM_NEQ, "0xa", StringUtils.asHex(10));
        assertEquals(AM_NEQ, "0x64", StringUtils.asHex(100));
    }
}
