package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class LatLongExampleTest {

    @Test
    public void testLatLongExample() {

        // This is to demonstrate how to use rtree to to do distance searches
        // with Lat Long points

        // Let's find all cities within 300km of Canberra

        Point sydney = Geometries.point(151.2094, -33.86);
        Point canberra = Geometries.point(149.1244, -35.3075);
        Point brisbane = Geometries.point(153.0278, -27.4679);
        RTree<String, Point> tree = RTree.star().create();
        tree = tree.add("Sydney", sydney);
        tree = tree.add("Brisbane", brisbane);

        // Now search for all locations within 300km of Canberra
        final double distanceKm = 300;
        List<Entry<String, Point>> list = search(tree, canberra, distanceKm)
        // get the result
                .toList().toBlocking().single();

        // should have returned Sydney only
        assertEquals(1, list.size());
        assertEquals("Sydney", list.get(0).value());
    }

    public static <T> Observable<Entry<T, Point>> search(RTree<T, Point> tree, Point lonLat,
            final double maxDistanceKm) {
        // First we need to calculate an enclosing lat long rectangle for this
        // distance then we refine on the exact distance
        final Position from = Position.create(lonLat.y(), lonLat.x());
        // this calculates a pretty accurate bounding box. Depending on the
        // performance you require you wouldn't have to be this accurate because
        // accuracy is enforced later
        Position north = from.predict(maxDistanceKm, 0);
        Position south = from.predict(maxDistanceKm, 180);
        Position east = from.predict(maxDistanceKm, 90);
        Position west = from.predict(maxDistanceKm, 270);

        Rectangle bounds = Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(),
                north.getLat());

        return tree
        // do the first search using the bounds
                .search(bounds)
                // refine using the exact distance
                .filter(new Func1<Entry<T, Point>, Boolean>() {
                    @Override
                    public Boolean call(Entry<T, Point> entry) {
                        Point p = entry.geometry();
                        Position position = Position.create(p.y(), p.x());
                        return from.getDistanceToKm(position) < maxDistanceKm;
                    }
                });
    }
}
