package com.github.davidmoten.rtree;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class Leaf implements Node {

	private final List<Entry> entries;
	private final Rectangle mbr;
	private final Context context;

	public Leaf(List<Entry> entries, Optional<NonLeaf> parent, Context context) {
		this.entries = entries;
		this.context = context;
		this.mbr = Util.mbr(entries);
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	public List<Entry> entries() {
		return entries;
	}

	@Override
	public NonLeaf add(Entry entry, Stack<NonLeaf> stack) {
		// TODO Auto-generated method stub
		return null;
	}

}
