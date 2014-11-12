package com.github.davidmoten.rtree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

final class ImageSaver {
    
    static void save(BufferedImage image, File file, String imageFormat) {
        try {
            ImageIO.write(image, imageFormat, file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
