package com.github.davidmoten.rtree;

import java.util.List;

public interface EntiriesSplitter {
	EntriesPair split(List<Entry> entries);
}
