package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Observable;
import rx.functions.Func1;

public class LatLongExampleTest {

    private static final Point sydney = Geometries.point(151.2094, -33.86);
    private static final Point canberra = Geometries.point(149.1244, -35.3075);
    private static final Point brisbane = Geometries.point(153.0278, -27.4679);
    private static final Point bungendore = Geometries.point(149.4500, -35.2500);

    @Test
    public void testLatLongExample() {

        // This is to demonstrate how to use rtree to to do distance searches
        // with Lat Long points

        // Let's find all cities within 300km of Canberra

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
            final double distanceKm) {
        // First we need to calculate an enclosing lat long rectangle for this
        // distance then we refine on the exact distance
        final Position from = Position.create(lonLat.y(), lonLat.x());
        Rectangle bounds = createBounds(from, distanceKm);

        return tree
                // do the first search using the bounds
                .search(bounds)
                // refine using the exact distance
                .filter(new Func1<Entry<T, Point>, Boolean>() {
                    @Override
                    public Boolean call(Entry<T, Point> entry) {
                        Point p = entry.geometry();
                        Position position = Position.create(p.y(), p.x());
                        return from.getDistanceToKm(position) < distanceKm;
                    }
                });
    }

    @Test
    public void testSearchLatLongCircles() {
        RTree<GeoCircleValue<String>, Rectangle> tree = RTree.star().create();
        // create circles around these major towns
        GeoCircleValue<String> sydneyCircle = createGeoCircleValue(sydney, 100, "Sydney");
        GeoCircleValue<String> canberraCircle = createGeoCircleValue(canberra, 50, "Canberra");
        GeoCircleValue<String> brisbaneCircle = createGeoCircleValue(brisbane, 200, "Brisbane");

        // add the circles to the RTree using the bounding box of the circle as
        // the geometry
        tree = add(tree, sydneyCircle);
        tree = add(tree, canberraCircle);
        tree = add(tree, brisbaneCircle);

        // now find the circles that contain bungendore (which is 30km from
        // Canberra)
        final Point location = bungendore;
        String result = tree.search(location)
                // filter on the exact distance from the centre of the GeoCircle
                .filter(new Func1<Entry<GeoCircleValue<String>, Rectangle>, Boolean>() {
                    Position from = Position.create(location.y(), location.x());

                    @Override
                    public Boolean call(Entry<GeoCircleValue<String>, Rectangle> entry) {
                        Position centre = Position.create(entry.value().lat, entry.value().lon);
                        return from.getDistanceToKm(centre) < entry.value().radiusKm;
                    }
                })
                // do the search (only expect one value)
                .toBlocking().single()
                // get the name of the GoeCircleValue returned
                .value().value;
        assertEquals("Canberra", result);
    }

    private static Rectangle createBounds(final Position from, final double distanceKm) {
        // this calculates a pretty accurate bounding box. Depending on the
        // performance you require you wouldn't have to be this accurate because
        // accuracy is enforced later
        Position north = from.predict(distanceKm, 0);
        Position south = from.predict(distanceKm, 180);
        Position east = from.predict(distanceKm, 90);
        Position west = from.predict(distanceKm, 270);

        return Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(), north.getLat());
    }

    private static <T> GeoCircleValue<T> createGeoCircleValue(Point point, double radiusKm,
            T value) {
        return new GeoCircleValue<T>(point.y(), point.x(), radiusKm, value);
    }

    private static <T> RTree<GeoCircleValue<T>, Rectangle> add(
            RTree<GeoCircleValue<T>, Rectangle> tree, GeoCircleValue<T> c) {
        return tree.add(c, createBounds(Position.create(c.lat, c.lon), c.radiusKm));
    }

    private static class GeoCircleValue<T> {

        GeoCircleValue(float lat, float lon, double radiusKm, T value) {
            this.lat = lat;
            this.lon = lon;
            this.radiusKm = radiusKm;
            this.value = value;
        }

        float lat;
        float lon;
        double radiusKm;
        T value;
    }
}
