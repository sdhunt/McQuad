/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * General utilities dealing with images.
 *
 * @author Simon Hunt
 */
public class ImageUtils {

    private static final String PNG = "png";

    /**
     * Writes the specified image to disk as a .png file.
     *
     * @param bi the buffered image to write
     * @param out the file to write the image into
     * @throws RuntimeException if an error occurred
     */
    public static void writeImageToDisk(BufferedImage bi, File out) {
        if (out.exists())
            if (!out.delete())
                throw new RuntimeException("Failed to delete old file: " + out);

        try {
            ImageIO.write(bi, PNG, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing .png: " + out, e);
        }
    }
}
