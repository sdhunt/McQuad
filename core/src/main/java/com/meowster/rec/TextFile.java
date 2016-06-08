/*
 * Copyright (c) 2016 Meowster.com
 */

package com.meowster.rec;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates textual data file.
 */
public class TextFile {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final File backingFile;
    private final List<String> contents = new ArrayList<>();

    /**
     * Creates a text file instance for the given backing file.
     *
     * @param backingFile the backing file
     */
    public TextFile(File backingFile) {
        this.backingFile = backingFile;
    }

    /**
     * Returns true if the backing file exists, false otherwise.
     *
     * @return true if backing file exists
     */
    public boolean exists() {
        return backingFile.exists();
    }

    @Override
    public String toString() {
        return "TextFile{" + backingFile + "}";
    }

    /**
     * Adds the given string to the in-memory contents of the file.
     *
     * @param s the string to add
     * @return self, for chaining
     */
    public TextFile add(String s) {
        contents.add(s);
        return this;
    }

    /**
     * Writes the current in-memory contents of the file to the backing file.
     */
    public void write() {
        try {
            Files.write(backingFile.toPath(), contents, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
