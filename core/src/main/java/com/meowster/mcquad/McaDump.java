package com.meowster.mcquad;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.meowster.util.StringUtils.printErr;
import static java.lang.System.exit;

/**
 * Command-line based region file analysis program, for dumping information
 * about MCA (Minecraft Anvil format) files.
 * <pre>
 *  Usage: com.meowster.mcquad.McaDump {region file}.
 * </pre>
 */
public class McaDump {

    private static final Pattern RE_REGION_FILE =
            Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");

    private McaDump(File mca) {
        Matcher m = RE_REGION_FILE.matcher(mca.getName());
        if (m.matches()) {
            int x = Integer.parseInt(m.group(1));
            int z = Integer.parseInt(m.group(2));
            Region region = new Region(x, z, mca);
            RegionDataDump.analyze(region);
        } else {
            printErr("No match?? {}", mca.getName());
            exit(1);
        }
    }

    private static int usage() {
        System.out.println("Usage: McaDump {region file}");
        return 2;
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            exit(usage());
        }
        File mca = new File(args[0]);
        if (!mca.canRead()) {
            printErr("Unable to read file: {}", args[0]);
            exit(usage());
        }
        new McaDump(mca);
    }
}
