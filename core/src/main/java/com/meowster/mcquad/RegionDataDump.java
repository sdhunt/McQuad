package com.meowster.mcquad;

import java.io.DataInputStream;

import static com.meowster.util.StringUtils.EOL;
import static com.meowster.util.StringUtils.printOut;

/**
 * Analysis of a region file.
 */
class RegionDataDump {

    private static final String HASH = " #";
    private static final String DOT = " .";

    private static final int NCHUNKS = 32;  // chunk dimension of region
    private static final int NBLOCKS = 16;  // block dimension of chunk

    private static final int CHUNKS_PER_REGION = NCHUNKS * NCHUNKS;


    private static Region region;
    private static int totalChunks;
    private static ChunkStuff[][] data;
    private static int singleX;
    private static int singleZ;

    /**
     * Analyze the region and output results.
     *
     * @param r the region to analyze
     */
    static void analyze(Region r) {
        region = r;
        totalChunks = 0;
        data = new ChunkStuff[NCHUNKS][NCHUNKS];

        // iterate over the chunks in the region...
        for (int cz = 0; cz < NCHUNKS; cz++) {
            for (int cx = 0; cx < NCHUNKS; cx++) {
                DataInputStream dis = region.getChunkDataStream(cx, cz);
                if (dis == null)
                    continue;

                totalChunks++;
                singleX = cx;
                singleZ = cz;

//                Chunk chunk = new Chunk(dis);
                data[cz][cx] = new ChunkStuff(null);
            }
        }

        outputResults();
    }


    private static void outputResults() {
        printOut("Region: {}; Chunks: {}/{}",
                region.regionFile().getName(),
                totalChunks,
                CHUNKS_PER_REGION
        );
        if (totalChunks == 1) {
            printOut("  Chunk coords [{}, {}]", singleX, singleZ);
        }
//        printOut(createMap());
    }

    private static String createMap() {
        StringBuilder sb = new StringBuilder();
        for (int cz = 0; cz < NCHUNKS; cz++) {
            for (int cx = 0; cx < NCHUNKS; cx++) {
                sb.append(data[cz][cx] != null ? HASH : DOT);
            }
            sb.append(EOL);
        }
        return sb.toString();
    }


    // retains info about a chunk
    private static class ChunkStuff {
        ChunkStuff(Chunk chunk) {
            // capture any data we need for dumping...
        }
    }
}
