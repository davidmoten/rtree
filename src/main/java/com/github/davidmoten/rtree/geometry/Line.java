package com.github.davidmoten.rtree.geometry;

public interface Line extends Geometry {

    double x1();

    double y1();

    double x2();

    double y2();

    boolean intersects(Line b);

    boolean intersects(Point point);

    boolean intersects(Circle circle);

}