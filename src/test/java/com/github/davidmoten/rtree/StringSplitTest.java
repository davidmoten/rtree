package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import rx.Observable;
import rx.observables.StringObservable;

public class StringSplitTest {

    // TODO remove this when 0.22.0 released
    // @Test
    public void testSplitOnEmptyStream() {
        assertEquals(0, (int) StringObservable.split(Observable.<String> empty(), "\n").count()
                .toBlocking().single());
    }

    // TODO remove thiese when 0.22 released of rxjava-string
    // @Test
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
