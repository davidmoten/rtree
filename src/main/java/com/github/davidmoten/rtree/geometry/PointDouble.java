package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.rtree.geometry.internal.RectangleDouble;

public final class PointDouble implements Point {

    private final double x;
    private final double y;

    private PointDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    static PointDouble create(double x, double y) {
        return new PointDouble(x, y);
    }

    static PointDouble create(float x, float y) {
        return new PointDouble(x, y);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return RectangleDouble.distance(x, y, x, y, r.x1(), r.y1(), r.x2(), r.y2());
    }

    public double distance(PointDouble p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(PointDouble p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return dx * dx + dy * dy;
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
        return Geometries.rectangle(Math.min(x, r.x1()), Math.min(y, r.y1()), Math.max(x, r.x2()),
                Math.max(y, r.y2()));
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
        return true;
    }

    @Override
    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    @Override
    public double distanceSquared(Point p) {
        double dx = x - p.x();
        double dy = y - p.y();
        return dx * dx + dy * dy;
    }

}