package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class Geometries {

    private Geometries() {
        // prevent instantiation
    }

    public static Point point(double x, double y) {
        return Point.create(x, y);
    }

    public static Rectangle rectangle(double x1, double y1, double x2, double y2) {
        return Rectangle.create(x1, y1, x2, y2);
    }

    public static Circle circle(double x, double y, double radius) {
        return Circle.create(x, y, radius);
    }

    public static Rectangle rectangleGeographic(double lon1, double lat1, double lon2,
            double lat2) {
        double x1 = normalizeLongitude(lon1);
        double x2 = normalizeLongitude(lon2);
        if (x2 < x1) {
            x2 += 360;
        }
        return rectangle(x1, lat1, x2, lat2);
    }

    public static Point pointGeographic(double lon, double lat) {
        return point(normalizeLongitude(lon), lat);
    }

    @VisibleForTesting
    static double normalizeLongitude(double d) {
        if (d == -180.0)
            return -180.0;
        else {
            double sign = Math.signum(d);
            double x = Math.abs(d) / 360;
            double x2 = (x - Math.floor(x)) * 360;
            if (x2 >= 180)
                x2 -= 360;
            return x2 * sign;
        }
    }

    public static Line line(double x1, double y1, double x2, double y2) {
        return Line.create((float) x1, (float) y1, (float) x2, (float) y2);
    }
}
