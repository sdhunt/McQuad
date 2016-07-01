/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.meowster.util.StringUtils.printErr;

/**
 * Encapsulates information about the Region Directory.
 *
 * @author Simon Hunt
 */
class RegionData {

    private static final Pattern RE_REGION_FILE =
            Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");

    private static final FilenameFilter REGION_FILE_FILTER =
            (dir, name) -> RE_REGION_FILE.matcher(name).matches();

    private final Bounds bounds = new Bounds();
    private final Map<Coord, Region> regionMap = new HashMap<>();

    private int hardBoundsMin = 0;
    private int hardBoundsMax = 0;
    private boolean useHardBounds = false;

    private ChunkHeatMap heatMap;

    /**
     * Loads the region data by processing the specified set of regions.
     *
     * @param regions the regions
     */
    void load(Set<Region> regions) {
        for (Region r : regions)
            loadRegion(r);
    }

    /**
     * Loads a single region into the data set.
     *
     * @param r the region to load
     */
    private void loadRegion(Region r) {
        if (useHardBounds && outOfBounds(r)) {
            printErr(" *** Out-of-Bounds *** {}", r);
            return;
        }

        /*
         * NOTE:
         *  There is a bug in the Minecraft server (observed in 1.10.2) that
         *  sometimes outputs region files containing a single chunk, with no
         *  bearing to where players have explored. We need to filter out these
         *  spurious regions.
         */
        if (!r.isMock() && r.chunkCount() < 2) {
            printErr(" *** Spurious *** {}", r);
            return;
        }

        bounds.add(r.coord());
        regionMap.put(r.coord(), r);
        if (heatMap != null) {
            heatMap.store(r);
        }
    }

    private boolean outOfBounds(Region r) {
        Coord c = r.coord();
        return (c.x() < hardBoundsMin || c.x() > hardBoundsMax ||
                c.z() < hardBoundsMin || c.z() > hardBoundsMax);
    }

    /**
     * Loads the region data by processing the given region directory. Note
     * that files are assumed to be mock (empty) region files, that are simply
     * present to provide the file names, for unit testing.
     *
     * @param regionDir the region directory
     * @return self, for chaining
     */
    RegionData loadMock(File regionDir) {
        return load(regionDir, true);
    }

    /**
     * Loads the region data by processing the given region directory.
     *
     * @param regionDir the region directory
     * @return self, for chaining
     */
    RegionData load(File regionDir) {
        return load(regionDir, false);
    }


    private RegionData load(File regionDir, boolean mock) {
        if (!regionDir.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + regionDir);

        File[] files = regionDir.listFiles(REGION_FILE_FILTER);
        if (files.length == 0) {
            printErr("No matching region files :(");
        }

        for (File f : files) {
            try {
                Matcher m = RE_REGION_FILE.matcher(f.getName());
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1));
                    int z = Integer.parseInt(m.group(2));
                    loadRegion(new Region(x, z, f, mock));
                } else {
                    printErr("No match?? {}", f.getName());
                }
            } catch (Exception e) {
                printErr("Region File error: {}", e);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "RegionData{" +
                "#Regions=" + regionMap.size() +
                ", bounds=" + bounds +
                '}';
    }

    /**
     * Returns the set of regions.
     *
     * @return the regions
     */
    Collection<Region> regions() {
        return Collections.unmodifiableCollection(regionMap.values());
    }

    /**
     * Returns the bounds for this region data set.
     *
     * @return the region bounds
     */
    Bounds bounds() {
        return bounds;
    }

    /**
     * Returns the region with the given coordinates, or null if no
     * such region exists.
     *
     * @param x region x-coord
     * @param z region z-coord
     * @return the region file, or null
     */
    Region at(int x, int z) {
        return regionMap.get(new Coord(x, z));
    }

    /**
     * Returns the region with the given coordinates, or null if no
     * such region exists.
     *
     * @param xz region coordinates
     * @return the region file, or null
     */
    Region at(Coord xz) {
        return regionMap.get(xz);
    }

    /**
     * Returns the number of regions contained in this data set.
     *
     * @return the number of regions
     */
    int size() {
        return regionMap.size();
    }

    /**
     * Sets the hard bounds for the region data. This limits the possible
     * coordinates of a region; any region files outside these bounds is
     * ignored.
     *
     * @param hardBounds min and max bound values
     * @return self, for chaining
     */
    RegionData hardBounds(int[] hardBounds) {
        hardBoundsMin = hardBounds[0];
        hardBoundsMax = hardBounds[1];
        useHardBounds = true;
        return this;
    }

    /**
     * Sets the heat map instance.
     *
     * @param heatMap heat map
     * @return self, for chaining
     */
    RegionData chunkHeatMap(ChunkHeatMap heatMap) {
        this.heatMap = heatMap;
        return this;
    }
}
