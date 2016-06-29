/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Encapsulates a Minecraft (.mca) Region File. Based on Togo's implementation,
 * although this implementation presents a read-only view of the data and does
 * not alter the region file in any way.
 *
 * @author Simon Hunt
 */

/*
 Region File Format

 Concept: The minimum unit of storage on hard drives is 4KB. 90% of Minecraft
 chunks are smaller than 4KB. 99% are smaller than 8KB. Write a simple
 container to store chunks in single files in runs of 4KB sectors.

 Each region file represents a 32x32 group of chunks. The conversion from
 chunk number to region number is floor(coord / 32): a chunk at (30, -3)
 would be in region (0, -1), and one at (70, -30) would be at (3, -1).
 Region files are named "r.x.z.data", where x and z are the region coordinates.

 A region file begins with a 4KB header that describes where chunks are stored
 in the file. A 4-byte big-endian integer represents sector offsets and sector
 counts. The chunk offset for a chunk (x, z) begins at byte 4*(x+z*32) in the
 file. The bottom byte of the chunk offset indicates the number of sectors the
 chunk takes up, and the top 3 bytes represent the sector number of the chunk.
 Given a chunk offset o, the chunk data begins at byte 4096*(o/256) and takes up
 at most 4096*(o%256) bytes. A chunk cannot exceed 1MB in size. If a chunk
 offset is 0, the corresponding chunk is not stored in the region file.

 Chunk data begins with a 4-byte big-endian integer representing the chunk data
 length in bytes, not counting the length field. The length must be smaller than
 4096 times the number of sectors. The next byte is a version field, to allow
 backwards-compatible updates to how chunks are encoded.

 A version of 1 represents a gzipped NBT file. The gzipped data is the chunk
 length - 1.

 A version of 2 represents a deflated (zlib compressed) NBT file. The deflated
 data is the chunk length - 1.
 */

class RegionFile {
    private static final String E_NO_SUCH_FILE = "No such region file: ";
    private static final String E_MALFORMED_REGION_FILE = "Malformed region file: ";

    private static final String R = "r";

    private static final long MASK_4K = 0xfff;
    private static final int NCHUNKS = 32;

    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;


    private final File fileName;
    private final int nSectors;
    private final int offsets[] = new int[SECTOR_INTS];
    private final int chunkTimestamps[] = new int[SECTOR_INTS];

    private RandomAccessFile file;
    private long lastModified = 0;

    /**
     * Constructs a region file instance for the given file on disk.
     *
     * @param path the region file path
     * @param mock if true, this is a mock file
     */
    RegionFile(File path, boolean mock) {
        if (!mock && !path.exists())
            throw new IllegalArgumentException(E_NO_SUCH_FILE);

        lastModified = path.lastModified();
        fileName = path;

        try {
            long fileLen;
            if (!mock) {
                file = new RandomAccessFile(path, R);
                fileLen = file.length();

                if (fileLen < SECTOR_BYTES)
                    malformed("length < 4KB");
                if ((fileLen & MASK_4K) != 0)
                    malformed("length not a multiple of 4KB");
            } else {
                fileLen = 0;
            }

            nSectors = (int) fileLen / SECTOR_BYTES;

            if (!mock) {
                // read first two sectors for chunk offsets and timestamps
                for (int i = 0; i < SECTOR_INTS; i++)
                    offsets[i] = file.readInt();
                for (int i = 0; i < SECTOR_INTS; i++)
                    chunkTimestamps[i] = file.readInt();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void malformed(String msg) {
        throw new RuntimeException(E_MALFORMED_REGION_FILE + msg);
    }

    @Override
    public String toString() {
        return "RegionFile{" +
                "fileName=" + fileName +
                ", nSectors=" + nSectors +
                ", lastModified=" + lastModified +
                '}';
    }

    /**
     * Returns the number of sectors in this region file.
     *
     * @return the number of sectors
     */
    int getSectorCount() {
        return nSectors;
    }

    /**
     * Returns an (uncompressed) stream of chunk data. If the chunk does not
     * exist (or if there is an error), null is returned.
     *
     * @param x chunk x-coord
     * @param z chunk z-coord
     * @return the chunk data stream
     */
    DataInputStream getChunkDataStream(int x, int z) {
        if (outOfBounds(x, z))
            return noStream(x, z, "Coordinates Out-of-Bounds!");

        int offset = getOffset(x, z);
        if (offset == 0)
            return null;    // no stream, but not an error condition either

        final int sectorNumber = offset >> 8;
        final int numSectors = offset & 0xff;

        if (sectorNumber + numSectors > nSectors)
            return noStream(x, z, "invalid sector");

        try {
            file.seek(sectorNumber * SECTOR_BYTES);
            final int length = file.readInt();

            if (length > SECTOR_BYTES * numSectors)
                return noStream(x, z,
                        "invalid length " + length + " > 4k * " + numSectors);

            byte version = file.readByte();
            if (version == VERSION_GZIP)
                return unzippedData(getDataStream(length));
            else if (version == VERSION_DEFLATE)
                return inflatedData(getDataStream(length));

            return noStream(x, z, "Unknown compression version: " + version);

        } catch (IOException e) {
            return noStream(x, z, "IOException: " + e.getMessage());
        }
    }

    /**
     * Returns the number of chunks available in this region.
     *
     * @return the chunk count
     */
    int chunkCount() {
        int nChunks = 0;
        for (int cz = 0; cz < NCHUNKS; cz++) {
            for (int cx = 0; cx < NCHUNKS; cx++) {
                if (chunkAvailableAt(cx, cz)) {
                    nChunks++;
                }
            }
        }
        return nChunks;
    }

    /**
     * Returns true if there is chunk data at the specified coordinates.
     *
     * @param x chunk x-coord
     * @param z chunk z-coord
     * @return true if chunk data is here; false otherwise
     */
    boolean chunkAvailableAt(int x, int z) {
        if (outOfBounds(x, z))
            return false;

        int offset = getOffset(x, z);
        if (offset == 0)
            return false;

        final int sectorNumber = offset >> 8;
        final int numSectors = offset & 0xff;

        return sectorNumber + numSectors <= nSectors;
    }

    /**
     * Returns the chunk timestamp for the specified coordinates.
     *
     * @param x chunk x-coord
     * @param z chunk z-coord
     * @return true if chunk data is here; false otherwise
     */
    int timeStampAt(int x, int z) {
        return outOfBounds(x, z) ? 0 : chunkTimestamps[x + z * NCHUNKS];
    }

    private boolean outOfBounds(int x, int z) {
        return x < 0 || x >= NCHUNKS || z < 0 || z >= NCHUNKS;
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * NCHUNKS];
    }

    private ByteArrayInputStream getDataStream(int length) throws IOException {
        byte[] data = new byte[length - 1];
        file.read(data);
        return new ByteArrayInputStream(data);
    }

    private DataInputStream inflatedData(ByteArrayInputStream data) {
        return new DataInputStream(new InflaterInputStream(data));
    }

    private DataInputStream unzippedData(ByteArrayInputStream data)
            throws IOException {
        return new DataInputStream(new GZIPInputStream(data));
    }

    private DataInputStream noStream(int x, int z, String msg) {
        String s = StringUtils.format("Chunk [{},{}] : {}", x, z, msg);
        System.err.println("Region: " + fileName.getName() + ": " + s);
        return null;
    }
}
