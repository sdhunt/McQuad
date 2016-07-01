/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Encapsulates files written to a subdirectory in the auxiliary directory.
 */
class AuxFiles {

    private static final String DASH = "-";
    private static final String DOT = ".";
    private static final String LATEST = "latest";
    private static final String PNG = "png";

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd");

    private final File subdir;
    private final String prefix;
    private final String suffix;

    /**
     * Creates an AuxFiles instance configured for the given auxiliary
     * directory, subdirectory name, file prefix and suffix. For example:
     * <pre>
     *     new AuxFiles(auxdir, "subdir", "prefix", "suffix")
     * </pre>
     * This will generate files of the form:
     * <pre>
     *     .../auxdir/subdir/prefix-YYYY-MM-DD.suffix
     *     .../auxdir/subdir/prefix-latest.suffix
     * </pre>
     *
     * @param auxdir     auxiliary directory reference
     * @param subdirName subdirectory name
     * @param prefix     file prefix
     * @param suffix     file suffix (without the dot)
     */
    AuxFiles(File auxdir, String subdirName, String prefix, String suffix) {
        subdir = new File(auxdir, subdirName);
        PathUtils.createIfNeedBe(subdir);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Creates an instance configured for the given auxiliary directory,
     * subdirectory name, file prefix, using a "png" suffix. For example:
     * <pre>
     *     new AuxFiles(auxdir, "subdir", "prefix")
     * </pre>
     * This will generate files of the form:
     * <pre>
     *     .../auxdir/subdir/prefix-YYYY-MM-DD.png
     *     .../auxdir/subdir/prefix-latest.png
     * </pre>
     *
     * @param auxdir     auxiliary directory reference
     * @param subdirName subdirectory name
     * @param prefix     file prefix
     */
    AuxFiles(File auxdir, String subdirName, String prefix) {
        this(auxdir, subdirName, prefix, PNG);
    }

    /**
     * Returns a file of the form:
     * <pre>
     *     .../auxdir/subdir/prefix-YYYY-MM-DD.suffix
     * </pre>
     *
     * @return date-stamped filename
     */
    File dated() {
        String today = FORMAT.format(new Date());
        return new File(subdir, prefix + DASH + today + DOT + suffix);
    }

    /**
     * Returns a file of the form:
     * <pre>
     *     .../auxdir/subdir/prefix-latest.suffix
     * </pre>
     *
     * @return "latest" filename
     */
    File latest() {
        return new File(subdir, prefix + DASH + LATEST + DOT + suffix);
    }
}
