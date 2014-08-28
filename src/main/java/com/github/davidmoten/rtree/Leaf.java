package com.github.davidmoten.rtree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.util.ImmutableStack;
import com.github.davidmoten.util.ListPair;
import com.google.common.base.Preconditions;

final class Leaf<T> implements Node<T> {

	private final List<Entry<T>> entries;
	private final Rectangle mbr;
	private final Context context;

	Leaf(List<Entry<T>> entries, Context context) {
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

	List<Entry<T>> entries() {
		return entries;
	}

	@Override
	public Node<T> add(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack) {
		Preconditions.checkNotNull(stack);
		if (entries.size() < context.maxChildren()) {
			final Leaf<T> leaf = new Leaf<T>(Util.add(entries, entry), context);
			return replace(this, leaf, stack, context);
		} else {
			final List<Entry<T>> newChildren = Util.add(entries, entry);
			final ListPair<Entry<T>> pair = context.splitter().split(
					newChildren);
			final Leaf<T> leaf1 = new Leaf<T>(pair.list1(), context);
			final Leaf<T> leaf2 = new Leaf<T>(pair.list2(), context);
			final List<Leaf<T>> list = Arrays.asList(leaf1, leaf2);
			return replace(this, list, stack, context);
		}
	}

	private static <R> Node<R> replace(Node<R> node, Node<R> replacement,
			ImmutableStack<NonLeaf<R>> stack, Context context) {
		return replace(node, Collections.singletonList(replacement), stack,
				context);
	}

	private static <R> Node<R> replace(Node<R> node,
			List<? extends Node<R>> replacements,
			ImmutableStack<NonLeaf<R>> stack, Context context) {
		Preconditions
				.checkArgument(replacements.size() < context.maxChildren());
		if (stack.isEmpty() && replacements.size() == 1)
			// the singleton replacement can be the new root node
			return replacements.get(0);
		else if (stack.isEmpty()) {
			// make a parent for the replacements and return that
			return new NonLeaf<R>(replacements);
		} else {
			final NonLeaf<R> n = stack.peek();
			final List<? extends Node<R>> newChildren = Util.replace(
					n.children(), node, replacements);
			if (n.children().size() < context.maxChildren()) {
				final NonLeaf<R> newNode = new NonLeaf<R>(newChildren);
				return replace(n, newNode, stack.pop(), context);
			} else {
				final ListPair<? extends Node<R>> pair = context.splitter()
						.split(newChildren);
				final NonLeaf<R> node1 = new NonLeaf<R>(pair.list1());
				final NonLeaf<R> node2 = new NonLeaf<R>(pair.list2());
				return replace(n, Arrays.asList(node1, node2), stack.pop(),
						context);
			}
		}
	}

	@Override
	public void search(Func1<? super Geometry, Boolean> criterion,
			Subscriber<? super Entry<T>> subscriber) {

		for (Entry entry : entries) {
			if (subscriber.isUnsubscribed())
				return;
			else {
				if (criterion.call(entry.geometry()))
					subscriber.onNext(entry);
			}
		}
	}

	@Override
	public String toString() {
		return "Leaf [entries=" + entries + ", mbr=" + mbr + "]";
	}

}
