package com.github.davidmoten.rtree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.util.ImmutableStack;
import com.github.davidmoten.util.ListPair;
import com.google.common.base.Preconditions;

final class Leaf implements Node {

	private final List<Entry> entries;
	private final Rectangle mbr;
	private final Context context;

	Leaf(List<Entry> entries, Context context) {
		Preconditions.checkNotNull(entries);
		Preconditions.checkNotNull(context);
		this.entries = entries;
		this.context = context;
		this.mbr = Util.mbr(entries);
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	List<Entry> entries() {
		return entries;
	}

	@Override
	public Node add(Entry entry, ImmutableStack<NonLeaf> stack) {
		Preconditions.checkNotNull(stack);
		if (entries.size() < context.maxChildren()) {
			final Leaf leaf = new Leaf(Util.add(entries, entry), context);
			return replace(this, leaf, stack, context);
		} else {
			final List<Entry> newChildren = Util.add(entries, entry);
			final ListPair<Entry> pair = context.splitter().split(newChildren);
			final Leaf leaf1 = new Leaf(pair.list1(), context);
			final Leaf leaf2 = new Leaf(pair.list2(), context);
			final List<Leaf> list = Arrays.asList(leaf1, leaf2);
			return replace(this, list, stack, context);
		}
	}

	private static Node replace(Node node, Node replacement,
			ImmutableStack<NonLeaf> stack, Context context) {
		return replace(node, Collections.singletonList(replacement), stack,
				context);
	}

	private static Node replace(Node node, List<? extends Node> replacements,
			ImmutableStack<NonLeaf> stack, Context context) {
		Preconditions
				.checkArgument(replacements.size() < context.maxChildren());
		if (stack.isEmpty() && replacements.size() == 1)
			// the singleton replacement can be the new root node
			return replacements.get(0);
		else if (stack.isEmpty()) {
			// make a parent for the replacements and return that
			return new NonLeaf(replacements);
		} else {
			final NonLeaf n = stack.peek();
			final List<? extends Node> newChildren = Util.replace(n.children(),
					node, replacements);
			if (n.children().size() < context.maxChildren()) {
				final NonLeaf newNode = new NonLeaf(newChildren);
				return replace(n, newNode, stack.pop(), context);
			} else {
				final ListPair<? extends Node> pair = context.splitter().split(
						newChildren);
				final NonLeaf node1 = new NonLeaf(pair.list1());
				final NonLeaf node2 = new NonLeaf(pair.list2());
				return replace(n, Arrays.asList(node1, node2), stack.pop(),
						context);
			}
		}
	}

	@Override
	public void search(Func1<? super Rectangle, Boolean> criterion,
			Subscriber<? super Entry> subscriber) {

		for (Entry entry : entries) {
			if (subscriber.isUnsubscribed())
				return;
			else {
				if (criterion.call(entry.mbr()))
					subscriber.onNext(entry);
			}
		}
	}

	@Override
	public String toString() {
		return "Leaf [entries=" + entries + ", mbr=" + mbr + "]";
	}

}
