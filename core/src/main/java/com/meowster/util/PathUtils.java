/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.util;

import java.io.File;

import static com.meowster.util.StringUtils.printErr;

/**
 * A set of utility methods dealing with paths, directories and files.
 *
 * @author Simon Hunt
 */
// TODO: UNIT TESTS
public class PathUtils {
    /**
     * Deletes the specified directory and everything underneath it.
     *
     * @param path the directory path
     * @return true if everything got deleted
     */
    public static boolean deleteTree(File path) {
        if (!path.exists())
            return false;

        boolean result = true;
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null)
                for (File f : files)
                    result = result && deleteTree(f);
        }
        return result && path.delete();
    }

    /**
     * Creates the specified directory.
     *
     * @param path the directory path
     */
    public static void makeDir(File path) {
        if (!path.mkdir())
            printErr("Failed to create output directory: {}", path);
    }

    /**
     * Tests the given path and attempts to create a directory if
     * one does not already exist yet.
     *
     * @param path directory path
     * @return true if a directory was created, false if already present
     */
    public static boolean createIfNeedBe(File path) {
        if (path.exists()) {
            return false;
        }
        makeDir(path);
        return true;
    }

    /**
     * Tests the given path (subdirectory) and attempts to create a directory
     * if one does not already exist yet.
     *
     * @param path  directory path
     * @param child the name of the subdirectory
     * @return true if a directory was created, false if already present
     */
    public static boolean createIfNeedBe(File path, String child) {
        File sub = new File(path, child);
        if (sub.exists()) {
            return false;
        }
        makeDir(sub);
        return true;
    }
}
