package com.github.davidmoten.rtree.geometry.internal;

import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

public final class GeometryUtil {

    private GeometryUtil() {
        // prevent instantiation
    }

    public static double distance(Point a, Point b) {
        return Math.sqrt(distanceSquared(a, b));
    }

    public static double distanceSquared(Point a, Point b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return dx * dx + dy * dy;
    }

    public static double max(double a, double b) {
        if (a < b)
            return b;
        else
            return a;
    }

    public static float max(float a, float b) {
        if (a < b)
            return b;
        else
            return a;
    }

    public static double min(double a, double b) {
        if (a < b)
            return a;
        else
            return b;
    }

    public static float min(float a, float b) {
        if (a < b)
            return a;
        else
            return b;
    }

    public static double distance(double x, double y, Rectangle r) {
        return distance(x, y, r.x1(), r.y1(), r.x2(), r.y2());
    }

    public static double distance(double x, double y, double a1, double b1, double a2, double b2) {
        return distance(x, y, x, y, a1, b1, a2, b2);
    }

    public static double distance(double x1, double y1, double x2, double y2, double a1, double b1,
            double a2, double b2) {
        if (intersects(x1, y1, x2, y2, a1, b1, a2, b2)) {
            return 0;
        }
        boolean xyMostLeft = x1 < a1;
        double mostLeftX1 = xyMostLeft ? x1 : a1;
        double mostRightX1 = xyMostLeft ? a1 : x1;
        double mostLeftX2 = xyMostLeft ? x2 : a2;
        double xDifference = max(0, mostLeftX1 == mostRightX1 ? 0 : mostRightX1 - mostLeftX2);

        boolean xyMostDown = y1 < b1;
        double mostDownY1 = xyMostDown ? y1 : b1;
        double mostUpY1 = xyMostDown ? b1 : y1;
        double mostDownY2 = xyMostDown ? y2 : b2;

        double yDifference = max(0, mostDownY1 == mostUpY1 ? 0 : mostUpY1 - mostDownY2);

        return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
    }

    public static boolean intersects(double x1, double y1, double x2, double y2, double a1,
            double b1, double a2, double b2) {
        return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2;
    }

}
