package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import rx.Observable;
import rx.observables.StringObservable;

public class StringSplitTest {
    @Test
    public void testSplitOnEmptyStream() {
        assertEquals(0, (int) StringObservable.split(Observable.<String> empty(), "\n").count()
                .toBlocking().single());
    }

    @Test
    public void testSplitOnStreamThatThrowsExceptionImmediately() {
        RuntimeException ex = new RuntimeException("boo");
        try {
            StringObservable.split(Observable.<String> error(ex), "\n").count().toBlocking()
                    .single();
            fail();
        } catch (RuntimeException e) {
            assertEquals(ex, e);
        }
    }
}
