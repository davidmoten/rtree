package com.github.davidmoten.rtree;

import java.util.Collections;
import java.util.List;

import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Preconditions;

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
	public Node add(Entry entry, ImmutableStack<NonLeaf> stack) {
		if (entries.size() < context.maxChildren()) {
			final Leaf leaf = new Leaf(Util.add(entries, entry), context);
			return replace(this, leaf, stack, context);
		} else {
			// TODO
			return null;
		}
	}

	private Node replace(Node node, Node replacement,
			ImmutableStack<NonLeaf> stack, Context context) {
		return replace(node, Collections.singletonList(replacement), stack,
				context);
	}

	private Node replace(Node node, List<Node> replacements,
			ImmutableStack<NonLeaf> stack, Context context) {
		Preconditions
				.checkArgument(replacements.size() < context.maxChildren());
		if (stack.isEmpty() && replacements.size() == 1)
			return replacements.get(0);
		else if (stack.isEmpty()) {
			// make a parent for the replacements and return tha
			return new NonLeaf(replacements, context);
		} else {
			final NonLeaf n = stack.peek();
			if (n.children().size() < context.maxChildren()) {
				final NonLeaf newNode = new NonLeaf(Util.replace(n.children(),
						node, replacements), context);
				return replace(n, newNode, stack.pop(), context);
			} else {
				// TODO
				return null;
			}
		}
	}
}
