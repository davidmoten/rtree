package com.github.davidmoten.rtree.geometry;

public interface RectangleDouble extends Rectangle {

    double intersectionAreaD(Rectangle r);

    double perimeterD();

    double x1d();

    double y1d();

    double x2d();

    double y2d();

    double areaD();

}
