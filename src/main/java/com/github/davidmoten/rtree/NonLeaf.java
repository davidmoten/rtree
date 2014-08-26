package com.github.davidmoten.rtree;

import java.util.List;
import java.util.Optional;

public class NonLeaf implements Node {

	private final Optional<NonLeaf> parent;
	private final List<Node> children;
	private final Rectangle mbr;
	private final Context context;

	public NonLeaf(Optional<NonLeaf> parent, List<Node> children,
			Rectangle mbr, Context context) {
		this.parent = parent;
		this.children = children;
		this.mbr = mbr;
		this.context = context;
	}

	@Override
	public Optional<NonLeaf> parent() {
		return parent;
	}

	public List<Node> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	/**
	 * Returns new root node after addition of entry.
	 * 
	 * @param entry
	 * @return
	 */
	public NonLeaf add(Entry entry) {
		// TODO
		return null;
	}
}
