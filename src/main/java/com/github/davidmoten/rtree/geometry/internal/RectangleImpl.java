package com.github.davidmoten.rtree.geometry.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

public final class RectangleImpl implements Rectangle {
    public final float x1, y1, x2, y2;

    private RectangleImpl(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static Rectangle create(float x1, float y1, float x2, float y2) {
        return new RectangleImpl(x1, y1, x2, y2);
    }

    @Override
    public double x1() {
        return x1;
    }

    @Override
    public double y1() {
        return y1;
    }

    @Override
    public double x2() {
        return x2;
    }

    @Override
    public double y2() {
        return y2;
    }

    @Override
    public double area() {
        return (x2 - x1) * (y2 - y1);
    }

    @Override
    public Rectangle add(Rectangle r) {
        if (r.isDoublePrecision()) {
            return RectangleDoubleImpl.create(min(x1, r.x1()), min(y1, r.y1()), max(x2, r.x2()),
                    max(y2, r.y2()));
        } else if (r instanceof Point) {
            Point p = (Point) r;
            return RectangleImpl.create(min(x1, p.x()), min(y1, p.y()), max(x2, p.x()), max(y2, p.y()));
        } else {
            RectangleImpl rf = (RectangleImpl) r;
            return RectangleImpl.create(min(x1, rf.x1), min(y1, rf.y1), max(x2, rf.x2),
                    max(y2, rf.y2));
        }

    }

    @Override
    public boolean contains(double x, double y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return intersects(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public double distance(Rectangle r) {
        return distance(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
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

    private static boolean intersects(double x1, double y1, double x2, double y2, double a1,
            double b1, double a2, double b2) {
        return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2;
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<RectangleImpl> other = ObjectsHelper.asClass(obj, RectangleImpl.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
        } else
            return false;
    }

    @Override
    public double intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else
            return RectangleDoubleImpl
                    .create(max(x1, r.x1()), max(y1, r.y1()), min(x2, r.x2()), min(y2, r.y2()))
                    .area();
    }

    @Override
    public double perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    private static double max(double a, double b) {
        if (a < b)
            return b;
        else
            return a;
    }

    private static float max(float a, float b) {
        if (a < b)
            return b;
        else
            return a;
    }

    private static double min(double a, double b) {
        if (a < b)
            return a;
        else
            return b;
    }

    private static float min(float a, float b) {
        if (a < b)
            return a;
        else
            return b;
    }

    @Override
    public boolean isDoublePrecision() {
        return false;
    }

    @Override
    public String toString() {
        return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
    }

}