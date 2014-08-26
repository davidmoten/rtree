package com.github.davidmoten.rtree;

import java.util.List;

public interface Splitter {
	<T extends HasMbr> ListPair<T> split(List<T> entries);
}
