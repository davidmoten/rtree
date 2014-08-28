package com.github.davidmoten.rtree.geometry;


public interface Geometry extends HasMbr {

	double distance(Rectangle r);

	boolean intersects(Rectangle r);

}
