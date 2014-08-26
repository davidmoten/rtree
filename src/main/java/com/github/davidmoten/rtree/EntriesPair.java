package com.github.davidmoten.rtree;

import java.util.List;

public class EntriesPair {
	private final List<Entry> entries1;
	private final List<Entry> entries2;

	public EntriesPair(List<Entry> entries1, List<Entry> entries2) {
		this.entries1 = entries1;
		this.entries2 = entries2;
	}

	public List<Entry> entries1() {
		return entries1;
	}

	public List<Entry> entries2() {
		return entries2;
	}

}
