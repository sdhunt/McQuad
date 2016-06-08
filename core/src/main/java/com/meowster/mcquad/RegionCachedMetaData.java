/*
 * Copyright (c) 2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.TextFile;
import com.meowster.util.StringUtils;

import java.io.File;
import java.util.Date;

/**
 * Encapsulates region meta data stored on disk, such as region access times
 * and other interesting attributes.
 */
public class RegionCachedMetaData {

    private static final String REGION_CACHE = "region-cache.txt";
    private static final String HEADER = "# Region Cache : %s";

    private final TextFile tf;

    private RegionCachedMetaData(File cacheFile) {
        tf = new TextFile(cacheFile);

        if (tf.exists()) {
            parseCacheFile();
        } else {
            createEmptyCacheFile();
        }
    }

    private void parseCacheFile() {
        StringUtils.printOut(">>> parseCacheFile()");
        // TODO: parse cache file to populate the cache map
    }

    private void createEmptyCacheFile() {
        StringUtils.printOut(">>> createEmptyCacheFile()");
        tf.add(String.format(HEADER, new Date()))
                .write();
    }

    @Override
    public String toString() {
        return "RegionCachedMetaData:{" + tf + "}";
    }

    /**
     * Load the region meta data from the file cache.
     *
     * @param metaDir base output directory
     * @return loaded meta data instance
     */
    public static RegionCachedMetaData load(File metaDir) {
        return new RegionCachedMetaData(new File(metaDir, REGION_CACHE));
    }
}
