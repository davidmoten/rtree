package com.github.davidmoten.rtree.geometry;

public interface Point extends Rectangle {

    double x();

    double y();

    double distance(Point p);

    double distanceSquared(Point p);
}
