/*
 * Copyright (c) 2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.PathUtils;

import java.io.File;

/**
 * General utilities regarding the output directory tree.
 */

public class OutputUtils {

    /*
      == Output Directory Layout ==

      outputDir, e.g.:  .../www/
                            +-- index.html
                            |
                            +-- aux/
                            |   +-- heatmap.png
                            |   :
                            |
                            +-- meta/
                            |   +-- region-cache.txt
                            |   :
                            |
                            +-- tiles/
                            |   +-- z1/
                            |   |   :
                            |   |
                            |   +-- z2/
                            |   |   :
                            |   :
                            |
                            +-- tp/
                                +-- polymaps/
                                    :
     */

    private static final String AUX = "aux";
    private static final String META = "meta";
    private static final String TILES = "tiles";
    private static final String TP = "tp";

    private final File outputDir;

    /**
     * Constructs a utils instance for the given root output directory.
     *
     * @param outputDir root output directory
     */
    public OutputUtils(File outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Returns the root output directory.
     *
     * @return root directory
     */
    public File rootDir() {
        return outputDir;
    }

    /**
     * Returns a reference to the aux directory.
     *
     * @return aux directory
     */
    public File auxDir() {
        return ensureDirectory(AUX);
    }

    /**
     * Returns a reference to the meta directory.
     *
     * @return meta directory
     */
    public File metaDir() {
        return ensureDirectory(META);
    }

    /**
     * Returns a reference to the tiles directory.
     *
     * @return tiles directory
     */
    public File tilesDir() {
        return ensureDirectory(TILES);
    }

    private File ensureDirectory(String dirName) {
        File dir = new File(outputDir, dirName);
        PathUtils.createIfNeedBe(dir);
        return dir;
    }
}
