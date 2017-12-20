package com.github.davidmoten.rtree.geometry.internal;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

public final class PointFloat implements Point {

    private final float x;
    private final float y;

    private PointFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static PointFloat create(float x, float y) {
        return new PointFloat(x, y);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return GeometryUtil.distance(x, y, r);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return r.x1() <= x && x <= r.x2() && r.y1() <= y && y <= r.y2();
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointFloat other = (PointFloat) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point [x=" + x() + ", y=" + y() + "]";
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public double x1() {
        return x;
    }

    @Override
    public double y1() {
        return y;
    }

    @Override
    public double x2() {
        return x;
    }

    @Override
    public double y2() {
        return y;
    }

    @Override
    public double area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
        if (r.isDoublePrecision()) {
            return RectangleDouble.create(Math.min(x, r.x1()), Math.min(y, r.y1()),
                    Math.max(x, r.x2()), Math.max(y, r.y2()));
        } else if (r instanceof RectangleFloat) {
            RectangleFloat rf = (RectangleFloat) r;
            return RectangleFloat.create(Math.min(x, rf.x1), Math.min(y, rf.y1), Math.max(x, rf.x2),
                    Math.max(y, rf.y2));
        } else if (r instanceof PointFloat) {
            PointFloat p = (PointFloat) r;
            return RectangleFloat.create(Math.min(x, p.x), Math.min(y, p.y), Math.max(x, p.x),
                    Math.max(y, p.y));
        } else {
            PointDouble p = (PointDouble) r;
            return RectangleDouble.create(Math.min(x, p.x()), Math.min(y, p.y()),
                    Math.max(x, p.x()), Math.max(y, p.y()));
        }
    }

    @Override
    public boolean contains(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public double intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public double perimeter() {
        return 0;
    }

    @Override
    public boolean isDoublePrecision() {
        return false;
    }

    public float xFloat() {
        return x;
    }

    public float yFloat() {
        return y;
    }

}