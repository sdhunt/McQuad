/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.rec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.meowster.util.StringUtils.*;

/**
 * A file-based implementation of {@link com.meowster.rec.RecordStore}.
 * This class is designed for extension, so that subclasses implement their
 * own records.
 *
 * @author Simon Hunt
 */
public class FileRecordStore implements RecordStore {
    private static final ClassLoader CL = FileRecordStore.class.getClassLoader();
    private static final String E_NULL_PARAM = "path cannot be null";
    private static final String E_BAD_FILE = "cannot read file: ";
    private static final String RE_RET = "\\r";
    private static final String RE_NL = "\\n";
    private static final String NUTHIN = "";
    private static final String RE_BLANK_LINE = "^\\s*$";
    private static final Pattern P_BLANK_LINE = Pattern.compile(RE_BLANK_LINE);


    private final String path;
    private final List<Record> records = new ArrayList<>();

    /**
     * Constructs a file record store using the given text file path.
     * Note that all lines beginning with the {@code "#"} character will
     * be suppressed automatically, as well as all blank lines.
     *
     * @param path the path of the file.
     * @throws NullPointerException if path is null
     * @throws IllegalArgumentException if the file cannot be read
     */
    public FileRecordStore(String path) {
        this(path, true);
    }

    /**
     * Constructs a file record store using the given text file path.
     * Note that all lines beginning with the {@code "#"} character will
     * be suppressed automatically. Blank lines will have records created
     * for them, unless the suppressBlankLines parameter is true.
     *
     * @param path the path of the file.
     * @param suppressBlankLines if true, will not create records for blank lines
     * @throws NullPointerException if path is null
     * @throws IllegalArgumentException if the file cannot be read
     */
    public FileRecordStore(String path, boolean suppressBlankLines) {
        if (path == null)
            throw new NullPointerException(E_NULL_PARAM);
        this.path = path;

        String s;
        try {
            s = getFileContents(path, CL);
        } catch (IOException e) {
            throw new IllegalArgumentException(E_BAD_FILE + path, e);
        }
        if (s == null)
            throw new IllegalArgumentException(E_BAD_FILE + path);

        process(stripCommentLines(s), suppressBlankLines);
    }

    private void process(String raw, boolean suppressBlankLines) {
        String clean = raw.replaceAll(RE_RET, NUTHIN);
        for (String s: clean.split(RE_NL)) {
            if (suppressBlankLines && P_BLANK_LINE.matcher(s).matches())
                continue;
            records.add(parseRawRecord(s));
        }
    }

    /**
     * Invoked for every non-comment line in the data file. (Remember that
     * blank lines may, or may not be suppressed).
     * <p>
     * If it is determined that no record should be produced, this method
     * should return null instead of a record.
     * <p>
     * This default implementation returns an unadorned record.
     *
     * @param s the raw line
     * @return a record for the line, (or null for no record)
     */
    protected Record parseRawRecord(String s) {
        return new DefaultRecord(s);
    }

    @Override
    public String toString() {
        return "FileRecordStore{" +
                "path='" + path + '\'' +
                ", #records=" + records.size() +
                '}';
    }

    /**
     * Returns a multi-line representation suitable for debugging.
     *
     * @return a multi-line string representation
     */
    public String toDebugString() {
        StringBuilder sb = new StringBuilder(toString());
        sb.append(EOL);
        for (Record r: records)
            sb.append(r).append(EOL);
        return sb.toString();
    }

    @Override
    public List<Record> getRecords() {
        return Collections.unmodifiableList(records);
    }

    @Override
    public int size() {
        return records.size();
    }

}
