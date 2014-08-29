package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.HasGeometry;

public interface Entry<T> extends HasGeometry {

	T object();

}
