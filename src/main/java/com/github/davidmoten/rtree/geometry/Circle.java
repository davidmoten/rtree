package com.github.davidmoten.rtree.geometry;

public class Circle implements Geometry {

    private final float x, y, radius;
    private final Rectangle mbr;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = Rectangle.create(x - radius, y - radius, x + radius, y + radius);
    }

    public static Circle create(double x, double y, double radius) {
        return new Circle((float) x, (float) y, (float) radius);
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, new Point(x, y).distance(r) - radius);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return new Point(x, y).distance(r) <= radius;
    }

}
