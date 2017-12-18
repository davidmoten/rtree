package com.github.davidmoten.rtree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

    float x1();

    float y1();

    float x2();

    float y2();

    float area();

    float intersectionArea(Rectangle r);

    float perimeter();

    Rectangle add(Rectangle r);

    boolean contains(double x, double y);

    double x1d();

    double y1d();

    double x2d();

    double y2d();

    double intersectionAreaD(Rectangle r);

    double perimeterD();

    double areaD();

}