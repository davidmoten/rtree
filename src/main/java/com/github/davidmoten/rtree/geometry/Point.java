package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.rtree.geometry.internal.RectangleImpl;

public final class Point implements Rectangle {

    private final float x;
    private final float y;

    private Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    static Point create(double x, double y) {
        return new Point((float) x, (float) y);
    }

    static Point create(float x, float y) {
        return new Point(x, y);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return RectangleImpl.distance(x, y, x, y, r.x1(), r.y1(), r.x2(), r.y2());
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(Point p) {
        float dx = x - p.x;
        float dy = y - p.y;
        return dx * dx + dy * dy;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return r.x1() <= x && x <= r.x2() && r.y1() <= y && y <= r.y2();
    }

    public float x() {
        return x;
    }

    public float y() {
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
        Point other = (Point) obj;
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
    public float x1() {
        return x;
    }

    @Override
    public float y1() {
        return y;
    }

    @Override
    public float x2() {
        return x;
    }

    @Override
    public float y2() {
        return y;
    }

    @Override
    public float area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
        return RectangleImpl.create(Math.min(x, r.x1()), Math.min(y, r.y1()), Math.max(x, r.x2()),
                Math.max(y, r.y2()));
    }

    @Override
    public boolean contains(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public float intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public float perimeter() {
        return 0;
    }

    @Override
    public double x1d() {
        return x;
    }

    @Override
    public double y1d() {
        return y;
    }

    @Override
    public double x2d() {
        return x;
    }

    @Override
    public double y2d() {
        return y;
    }

    @Override
    public double intersectionAreaD(Rectangle r) {
        return intersectionArea(r);
    }

    @Override
    public double perimeterD() {
        return perimeter();
    }

    @Override
    public double areaD() {
        return area();
    }

}