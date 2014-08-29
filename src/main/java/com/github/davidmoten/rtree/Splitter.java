package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.util.ListPair;

public interface Splitter {
    <T extends HasGeometry> ListPair<T> split(List<T> items);
}
