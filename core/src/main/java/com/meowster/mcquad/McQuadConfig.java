package com.meowster.mcquad;

import com.meowster.rec.TextFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persistent configuration.
 */
class McQuadConfig {

    private static final String CONFIG_FILE = "mcquad-config.txt";
    private static final String HEADER = "# McQuad Config : %s";
    private static final String FMT_BOUNDS = "RegionBounds: %d %d";

    private static final int REGION_MIN = -32;
    private static final int REGION_MAX = 31;

    private final TextFile tf;

    // ignore any region file outside these bounds.
    private int regionMin = REGION_MIN;
    private int regionMax = REGION_MAX;

    private static final Pattern P_REGION_BOUNDS =
            Pattern.compile("^RegionBounds:\\s(-?\\d+)\\s(-?\\d+)");

    private McQuadConfig(File file) {
        tf = new TextFile(file);

        if (tf.exists()) {
            parseConfig();
        } else {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        tf.clear()
                .add(header())
                .add(hardRegionBounds())
                .write();
    }

    private String hardRegionBounds() {
        return String.format(FMT_BOUNDS, REGION_MIN, REGION_MAX);
    }

    private String header() {
        return String.format(HEADER, new Date());
    }

    private void parseConfig() {
        List<String> lines = tf.nonCommentLines();
        for (String line : lines) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        Matcher m = P_REGION_BOUNDS.matcher(line);
        if (m.matches()) {
            regionMin = Integer.valueOf(m.group(1));
            regionMax = Integer.valueOf(m.group(2));
        }
    }

    @Override
    public String toString() {
        return String.format("McQuadConfig{ RegionBounds: %d %d }",
                regionMin, regionMax);
    }

    static McQuadConfig load(File metaDir) {
        return new McQuadConfig(new File(metaDir, CONFIG_FILE));
    }

    /**
     * Returns the min and max region bounds.
     *
     * @return min and max values
     */
    int[] regionBounds() {
        return new int[]{ regionMin, regionMax};
    }
}
