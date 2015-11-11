package com.github.davidmoten.rtree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

final class ImageSaver {

    private ImageSaver() {
        // prevent instantiation
    }

    static void save(final BufferedImage image, final File file, final String imageFormat) {
        // this is an ugly workaround to achieve 100% coverage (the pesky
        // IOException catch)
        // The performance hit of making this allocation should be negligible
        // compared to the cost of IO
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ImageIO.write(image, imageFormat, file);
                return null;
            }
        };
        run(callable);
    }

    static void run(Callable<Void> callable) {
        try {
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
