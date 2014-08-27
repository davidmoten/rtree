package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Preconditions;

public class NonLeaf implements Node {

	private final List<? extends Node> children;
	private final Rectangle mbr;
	private final Context context;

	public NonLeaf(List<? extends Node> children, Context context) {
		Preconditions.checkArgument(!children.isEmpty());
		this.children = children;
		this.mbr = Util.mbr(children);
		this.context = context;
	}

	public List<? extends Node> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public NonLeaf add(Entry entry, ImmutableStack<NonLeaf> stack) {
		return null;
	}
}
