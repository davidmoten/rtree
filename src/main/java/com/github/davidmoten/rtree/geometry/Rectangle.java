package com.github.davidmoten.rtree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

    double x1();

    double y1();

    double x2();

    double y2();

    double area();

    double intersectionArea(Rectangle r);

    double perimeter();

    Rectangle add(Rectangle r);

    boolean contains(double x, double y);
    
    boolean isDoublePrecision();

}