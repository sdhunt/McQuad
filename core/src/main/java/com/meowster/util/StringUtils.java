/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A set of utility methods dealing with Strings.
 *
 * @author Simon Hunt
 */
public class StringUtils {

    /** The platform dependent line separator (new line character). */
    public static final String EOL = System.getProperty("line.separator");

    /** String representing UTF-8 encoding (e.g. String.getBytes(UTF8)) */
    public static final String UTF8 = "UTF-8";

    /** Format token used for parameter replacement. */
    private static final String FORMAT_TOKEN = "{}";

    /** String representation of null. */
    private static final String NULL_REP = "{null}";

    /** Hex string prefix. */
    private static final String OX = "0x";


    /**
     * A simple string formatter that replaces each occurrence of
     * <code>{}</code> in the format string with the string representation
     * of each of the subsequent, optional arguments.
     * For example:
     * <pre>
     *     StringUtils.format("{} => {}", "Foo", 123);
     *     // returns "Foo => 123"
     * </pre>
     *
     * @param fmt the format string
     * @param o arguments to be inserted into the output string
     * @return a formatted string
     */
    public static String format(String fmt, Object... o) {
        if (fmt == null)
            throw new NullPointerException("null format string");
        if (o.length == 0)
            return fmt;

        // Format the message using the format string as the seed.
        // Stop either when the list of objects is exhausted or when
        // there are no other place-holder tokens.
        final int ftlen = FORMAT_TOKEN.length();
        int i = 0;
        int p = -1;
        String rep;
        StringBuilder sb = new StringBuilder(fmt);
        while (i < o.length && (p = sb.indexOf(FORMAT_TOKEN, p + 1)) >= 0) {
            rep = o[i] == null ? NULL_REP : o[i].toString();
            sb.replace(p, p + ftlen, rep);
            i++;
        }
        return sb.toString();
    }

    /**
     * Prints a formatted set of items to the specified print stream.
     * @see #format
     *
     * @param s the print stream to write to
     * @param fmt the string format
     * @param items the items
     */
    public static void printToStream(PrintStream s, String fmt, Object... items) {
        s.println(format(fmt, items));
    }

    /**
     * Prints a formatted set of items to STDOUT, using the
     * specified format string and items.
     * @see #format
     *
     * @param fmt the string format
     * @param items the items
     */
    public static void printOut(String fmt, Object... items) {
        printToStream(System.out, fmt, items);
    }

    /**
     * Prints the specified object to STDOUT, using its toString() method,
     * ending with a newline.
     *
     * @param o the object to be printed
     */
    public static void printOut(Object o) {
        System.out.println(o);
    }

    /**
     * Prints the specified object to STDOUT, using its toString() method (but
     * no newline).
     *
     * @param o the object to be printed
     */
    public static void print(Object o) {
        System.out.print(o);
    }

    /**
     * Prints a formatted set of items to STDERR, using the
     * specified format string and items.
     * @see #format
     *
     * @param fmt the string format
     * @param items the items
     */
    public static void printErr(String fmt, Object... items) {
        printToStream(System.err, fmt, items);
    }

    /**
     * Prints a space delimited line of items to STDOUT.
     *
     * @param items the items
     */
//    public static void print(Object... items) {
//        for (Object o: items) {
//            System.out.print(o);
//            System.out.print("  ");
//        }
//        System.out.println();
//    }



    /**
     * Read in the contents of a text file from the given path. If the
     * resource cannot be found, null is returned.
     *
     * @param path path of resource containing the data
     * @param classLoader class-loader to be used for locating resource
     * @return the contents of the file as a string
     * @throws java.io.IOException if there is a problem reading the file.
     */
    public static String getFileContents(String path, ClassLoader classLoader)
            throws IOException {
        InputStream is = classLoader.getResourceAsStream(path);
        if (is == null)
            return null;

        String result = null;
        try {
            // use Apache Commons IOUtils to read byte array from input stream
            byte[] bytes = IOUtils.toByteArray(is);
            result = new String(bytes, UTF8).replaceAll("\0", "");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // What to do, what to do?
            }
        }
        return result;
    }


    private static final Pattern RE_MULTLINE =
            Pattern.compile("^(.*)$", Pattern.MULTILINE);

    /**
     * Removes all lines that begin with the '#' character.
     *
     * @param s the multiline input
     * @return multiline output with "comment" lines removed
     */
    public static String stripCommentLines(String s) {
        StringBuilder sb = new StringBuilder();
        Matcher m = RE_MULTLINE.matcher(s);
        while(m.find()) {
            String line = m.group(1);
            if (!line.startsWith("#")) {
                sb.append(line).append(EOL);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the long value represented by the given hex string. Leading
     * {@code "0x"} is stripped if present.
     * <pre>
     *     long a1 = Utils.hexAsLong("0x0a");
     *     long a2 = Utils.hexAsLong("0a");
     * </pre>
     *
     * @param s the hex string to evaluate
     * @return the long value
     * @throws NumberFormatException if input is not valid
     */
    public static long hexAsLong(String s) {
        String h = s.startsWith("0x") ? s.substring(2) : s;
        return Long.parseLong(h, 16);
    }

    /**
     * Returns the integer value represented by the given hex string. Leading
     * {@code "0x"} is stripped if present.
     * <pre>
     *     long a1 = Utils.hex("0x0a");
     *     long a2 = Utils.hex("0a");
     * </pre>
     *
     * @param s the hex string to evaluate
     * @return the integer value
     * @throws NumberFormatException if input is not valid
     */
    public static int hex(String s) {
        String h = s.startsWith("0x") ? s.substring(2) : s;
        return Integer.parseInt(h, 16);
    }

    /**
     * Returns a hex string representation of the given long value.
     *
     * @param val the value
     * @return a string representing the value in hex
     */
    public static String asHex(long val) {
        String h = Long.toHexString(val);
        return OX + h;
    }

    /**
     * Returns a hex string representation of the given int value.
     *
     * @param val the value
     * @return a string representing the value in hex
     */
    public static String asHex(int val) {
        String h = Integer.toHexString(val);
        return OX + h;
    }

}
