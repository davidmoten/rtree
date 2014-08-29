package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasMbr;

public interface Entry<T> extends HasMbr {

	T object();

	Geometry geometry();

}
