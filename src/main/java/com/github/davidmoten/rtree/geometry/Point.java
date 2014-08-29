package com.github.davidmoten.rtree.geometry;

public class Point implements Geometry {

    private final Rectangle mbr;

    public Point(float x, float y) {
        this.mbr = Rectangle.create(x, y, x, y);
    }

    public static Point create(double x, double y) {
        return new Point((float) x, (float) y);
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return mbr.distance(r);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return mbr.intersects(r);
    }

    public float x() {
        return mbr.x1();
    }

    public float y() {
        return mbr.y1();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mbr == null) ? 0 : mbr.hashCode());
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
        if (mbr == null) {
            if (other.mbr != null)
                return false;
        } else if (!mbr.equals(other.mbr))
            return false;
        return true;
    }

}
