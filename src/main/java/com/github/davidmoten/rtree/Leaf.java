package com.github.davidmoten.rtree;

import java.util.List;
import java.util.Stack;

public class Leaf implements Node {

	private final List<Entry> entries;
	private final Rectangle mbr;
	private final Context context;

	public Leaf(List<Entry> entries, Context context) {
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
		if (entries.size() < context.maxChildren()) {
			final Leaf leaf = new Leaf(Util.add(entries, entry), context);
			return replace(this, leaf, stack, context);
		} else {
			// TODO
			return null;
		}
	}

	private NonLeaf replace(Node node, Node replacement, Stack<NonLeaf> stack,
			Context context) {
		if (stack.isEmpty())
			return (NonLeaf) replacement;
		else {
			final NonLeaf n = stack.pop();
			if (n.children().size() < context.maxChildren()) {
				final NonLeaf newNode = new NonLeaf(Util.replace(n.children(),
						node, replacement), context);
				return replace(n, newNode, stack, context);
			} else {
				// TODO
				return null;
			}
		}
	}
}
