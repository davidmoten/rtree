package com.github.davidmoten.rtree;

public interface Geometry extends HasMbr {

	double distance(Rectangle r);

	boolean intersects(Rectangle r);

}
