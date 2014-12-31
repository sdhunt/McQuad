/*
 * Copyright (c) 2014 Meowster.com
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
public class RegionData {

    private static final Pattern RE_REGION_FILE =
            Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");

    private static final FilenameFilter REGION_FILE_FILTER =
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return RE_REGION_FILE.matcher(name).matches();
                }
            };


    private final Bounds bounds = new Bounds();
    private final Map<Coord, Region> regionMap = new HashMap<>();

    /**
     * Loads the region data by processing the specified set of regions.
     *
     * @param regions the regions
     */
    public void load(Set<Region> regions) {
        for (Region r: regions)
            loadRegion(r);
    }

    /**
     * Loads a single region into the data set.
     *
     * @param r the region to load
     */
    private void loadRegion(Region r) {
        bounds.add(r.coord());
        regionMap.put(r.coord(), r);
    }

    /**
     * Loads the region data by processing the given region directory. Note
     * that files are assumed to be mock (empty) region files, that are simply
     * present to provide the file names, for unit testing.
     *
     * @param regionDir the region directory
     * @return self, for chaining
     */
    public RegionData loadMock(File regionDir) {
        return load(regionDir, true);
    }

    /**
     * Loads the region data by processing the given region directory.
     *
     * @param regionDir the region directory
     * @return self, for chaining
     */
    public RegionData load(File regionDir) {
        return load(regionDir, false);
    }


    private RegionData load(File regionDir, boolean mock) {
        if (!regionDir.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + regionDir);

        File[] files = regionDir.listFiles(REGION_FILE_FILTER);
        if (files.length == 0) {
            printErr("No matching region files :(");
        }

        for (File f: files) {
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
    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(regionMap.values());
    }

    /**
     * Returns the bounds for this region data set.
     *
     * @return the region bounds
     */
    public Bounds bounds() {
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
    public Region at(int x, int z) {
        return regionMap.get(new Coord(x, z));
    }

    /**
     * Returns the number of regions contained in this data set.
     *
     * @return the number of regions
     */
    public int size() {
        return regionMap.size();
    }
    // ===================================================================

}
