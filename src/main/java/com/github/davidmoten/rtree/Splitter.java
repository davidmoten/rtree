package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.util.ListPair;

public interface Splitter {
	<T extends HasMbr> ListPair<T> split(List<T> entries);
}
