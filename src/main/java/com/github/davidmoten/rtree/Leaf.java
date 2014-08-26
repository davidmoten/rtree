package com.github.davidmoten.rtree;

import java.util.List;
import java.util.Optional;

public class Leaf implements Node {

	private final Optional<NonLeaf> parent;
	private final List<Entry> entries;
	private final Rectangle mbr;

	public Leaf(List<Entry> entries, Optional<NonLeaf> parent) {
		this.entries = entries;
		this.parent = parent;
		this.mbr = Util.mbr(entries);
	}

	@Override
	public Optional<NonLeaf> parent() {
		return parent;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	public List<Entry> entries() {
		return entries;
	}

}
