/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.DefaultRecord;
import com.meowster.rec.FileRecordStore;
import com.meowster.rec.Record;
import com.meowster.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * An encapsulation of the biome-colors data file.
 *
 * @author Simon Hunt
 */
class BiomeColorsStore extends FileRecordStore {
    private static final String PATH = "com/meowster/mcquad/biome-colors.txt";
    private static final String DEFAULT = "default";

    /**
     * Constructs the store from the default file location.
     */
    public BiomeColorsStore() {
        super(PATH);
    }

    @Override
    protected Record parseRawRecord(String s) {
        return new BiomeRecord(s);
    }

    private static final String RE_BIOME_RECORD =
            "(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)(\\s+#\\s(.*)\\s+(\\d+\\s+\\d+))?";

    /**
     * Our record implementation. Note that the default record will have
     * biomeId set to null.
     */
    static class BiomeRecord extends DefaultRecord {
        private static final Pattern P_BIOME_RECORD = compile(RE_BIOME_RECORD);

        private BiomeId biomeId;
        private Color grassShading;
        private Color foliageShading;
        private Color waterShading;
        private String comment;
        private String numbersStr;

        BiomeRecord(String raw) {
            super(raw);
        }

        @Override
        protected void parseRaw(String raw) {
            Matcher m = P_BIOME_RECORD.matcher(raw);
            if (m.matches()) {
                biomeId = makeBiomeId(m.group(1));
                grassShading = new Color(m.group(2));
                foliageShading = new Color(m.group(3));
                waterShading = new Color(m.group(4));
                comment = m.group(6) == null ? null : m.group(6).trim();
                numbersStr = m.group(7);
            }
        }

        private BiomeId makeBiomeId(String idStr) {
            if (idStr.equals(DEFAULT))
                return null;
            return new BiomeId(StringUtils.hex(idStr));
        }

        @Override
        public String toString() {
            return "BiomeRecord{" +
                    "biomeId=" + biomeId +
                    ", grassShading=" + grassShading.hex() +
                    ", foliageShading=" + foliageShading.hex() +
                    ", waterShading=" + waterShading.hex() +
                    ", comment='" + comment + '\'' +
                    '}';
        }

        BiomeId biomeId() {
            return biomeId;
        }

        Color grass() {
            return grassShading;
        }

        Color foliage() {
            return foliageShading;
        }

        Color water() {
            return waterShading;
        }

        String comment() {
            return comment;
        }
    }
}
