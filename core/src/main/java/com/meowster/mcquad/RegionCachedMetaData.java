/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.rec.TextFile;
import com.meowster.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates region meta data stored on disk, such as region access times
 * and other interesting attributes.
 */
class RegionCachedMetaData {

    private static final String REGION_CACHE = "region-cache.txt";
    private static final String HEADER = "# Region Cache : %s";
    private static final int MAX_TTL = 15;

    private final TextFile tf;
    private final Map<Coord, MetaRegion> metaMap = new TreeMap<>();
    private Set<MetaRegion> toProcess;
    private Coord quadShift = null;

    private RegionCachedMetaData(File cacheFile) {
        tf = new TextFile(cacheFile);

        if (tf.exists()) {
            parseCacheFile();
        } else {
            createEmptyCacheFile();
        }
    }

    private void parseCacheFile() {
        metaMap.clear();
        List<String> lines = tf.nonCommentLines();

        for (String s : lines) {
            MetaRegion mr = MetaRegion.metaRegion(s);
            if (mr == null) {
                if (!matchQuadShift(s)) {
                    StringUtils.printErr("Failed to parse meta region [{}]", s);
                }
            } else {
                metaMap.put(mr.coord(), mr);
            }
        }
    }

    private static final Pattern P_QUAD_SHIFT =
            Pattern.compile("^quad-shift:\\s(\\[-?\\d+,-?\\d+\\])");

    private boolean matchQuadShift(String s) {
        Matcher m = P_QUAD_SHIFT.matcher(s);
        if (m.matches()) {
            quadShift = Coord.coord(m.group(1));
            return true;
        }
        return false;
    }

    private String header() {
        return String.format(HEADER, new Date());
    }

    private void createEmptyCacheFile() {
        metaMap.clear();
        tf.clear()
                .add(header())
                .write();
    }

    @Override
    public String toString() {
        return "RegionCachedMetaData:{" + size() + " records [" +
                toProcess.size() + " stale]}";
    }

    /**
     * Returns the number of meta regions.
     *
     * @return number of meta regions
     */
    int size() {
        return metaMap.size();
    }

    /**
     * Writes the current in-memory data to the backing file.
     */
    void persist() {
        tf.clear()
                .add(header());
        for (MetaRegion mr : metaMap.values()) {
            tf.add(mr.stringRecord());
        }
        tf.add("# === end of region data === <" + metaMap.size() + " regions>")
                .add("quad-shift: " + quadShift)
                .add("# === end of file ===")
                .write();
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

    /**
     * Updates our current state using information from the loaded regions.
     *
     * @param regionData data about the regions
     */
    void update(RegionData regionData) {
        toProcess = new HashSet<>();

        for (Region region : regionData.regions()) {
            MetaRegion mr = metaMap.get(region.coord());
            if (mr == null) {
                mr = new MetaRegion(region.coord());
                metaMap.put(mr.coord(), mr);
            }
            if (region.modified() > mr.touched()) {
                mr.setTouched(region.modified())
                        .setTtl(MAX_TTL);
                toProcess.add(mr);
            } else {
                mr.decTtl();
            }
        }
    }

    /**
     * Returns a set of region coordinates that require re-rendering.
     *
     * @return stale region coordinates
     */
    Set<Coord> stale() {
        Set<Coord> stale = new HashSet<>(toProcess.size());
        for (MetaRegion mr : toProcess) {
            stale.add(mr.coord());
        }
        return stale;
    }

    /**
     * Returns true if the calibration has changed since last time.
     *
     * @param calibration current calibration
     * @return true if change in calibration occurred
     */
    boolean updateQuadShift(Coord calibration) {
        boolean changed = !calibration.equals(quadShift);
        quadShift = calibration;
        return changed;
    }

    /**
     * Returns a map of (region occupied) coordinates and their TTL value.
     *
     * @return region TTL map
     */
    Map<Coord, Integer> regionTtlMap() {
        Map<Coord, Integer> map = new HashMap<>();
        for (MetaRegion mr: metaMap.values()) {
            map.put(mr.coord(), mr.ttl());
        }
        return map;
    }
}
