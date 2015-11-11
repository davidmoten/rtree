package com.github.davidmoten.rtree;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class ImageSaverTest {

    @Test
    public void testSaveImageToNonExistentDirectoryThrowsRuntimeException() {
        Visualizer v = RTree.create().visualize(100, 100);
        v.save("target/saved-image");
    }

    @Test(expected = RuntimeException.class)
    public void testRunThatThrows() {
        ImageSaver.run(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                throw new IOException();
            }
        });
    }

    @Test
    public void testRunThatDoesNotThrow() {
        final AtomicBoolean b = new AtomicBoolean();
        ImageSaver.run(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                b.set(true);
                return null;
            }
        });
        assertTrue(b.get());
    }

    @Test
    public void testIsUtilClass() {
        Asserts.assertIsUtilityClass(ImageSaver.class);
    }

}
