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
 * An encapsulation of the block-colors data file.
 *
 * @author Simon Hunt
 */
class BlockColorsStore extends FileRecordStore {
    private static final String PATH = "com/meowster/mcquad/block-colors.txt";
    private static final String DEFAULT = "default";

    /**
     * Constructs the store from the default file location.
     */
    public BlockColorsStore() {
        super(PATH);
    }

    @Override
    protected Record parseRawRecord(String s) {
        return new BlockRecord(s);
    }


    private static final String RE_BLOCK_RECORD =
            "(\\w+)(:(\\w+))?\\s+(\\w+)\\s*(biome_(\\w+))?\\s*(#\\s+)?(.*)";

    /**
     * Our record implementation. Note that the default record will have
     * blockId set to null.
     */
    static class BlockRecord extends DefaultRecord {
        private static final Pattern P_BLOCK_RECORD = compile(RE_BLOCK_RECORD);

        private BlockId blockId;
        private Color color;
        private BiomeInfluence biome;
        private String comment;


        BlockRecord(String raw) {
            super(raw);
        }

        @Override
        protected void parseRaw(String raw) {
            Matcher m = P_BLOCK_RECORD.matcher(raw);
            if (m.matches()) {
                blockId = makeBlockId(m.group(1), m.group(3));
                color = new Color(m.group(4));
                biome = BiomeInfluence.from(m.group(6));
                comment = m.group(8);
            }
        }

        private BlockId makeBlockId(String idStr, String dvStr) {
            if (idStr.equals(DEFAULT))
                return null;
            int id = StringUtils.hex(idStr);
            int dv = (dvStr == null) ? -1 : StringUtils.hex(dvStr);
            return new BlockId(id, dv);
        }

        @Override
        public String toString() {
            return "BlockRecord{" +
                    "blockId=" + blockId +
                    ", color=" + color.hex() +
                    ", biomeInfl=" + biome +
                    ", comment='" + comment + '\'' +
                    '}';
        }

        BlockId blockId() {
            return blockId;
        }

        Color color() {
            return color;
        }

        BiomeInfluence biomeInfluence() {
            return biome;
        }

        String comment() {
            return comment;
        }
    }

}
