package com.github.davidmoten.rtree;

import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import com.github.davidmoten.rtree.OnSubscribeSearch.SearchProducer;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.internal.LeafDefault;

import rx.Subscriber;
import rx.functions.Func1;

public class OnSubscribeSearchTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchProducerThrowsExceptionFromRequestAll() {
        Node<Integer, Geometry> node = Mockito.mock(Node.class);
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        Subscriber<Entry<Integer, Geometry>> subscriber = Mockito.mock(Subscriber.class);
        RuntimeException error = new RuntimeException();
        Mockito.doThrow(error).when(node).searchWithoutBackpressure(condition, subscriber);
        SearchProducer<Integer, Geometry> p = new OnSubscribeSearch.SearchProducer<Integer, Geometry>(
                node, condition, subscriber);
        p.request(Long.MAX_VALUE);
        Mockito.verify(subscriber).onError(error);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchProducerThrowsExceptionFromRequestSome() {
        Node<Integer, Point> node = new LeafDefault<Integer, Point>(Collections.singletonList(Entries.entry(
                1, Geometries.point(1, 1))), null);

        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        Subscriber<Entry<Integer, Point>> subscriber = new Subscriber<Entry<Integer, Point>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Integer, Point> t) {

            }
        };
        SearchProducer<Integer, Point> p = new OnSubscribeSearch.SearchProducer<Integer, Point>(
                node, condition, subscriber);
        p.request(1);
    }

}
