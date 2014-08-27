package com.github.davidmoten.rtree;

import java.util.List;

import rx.Subscriber;
import rx.functions.Func2;

import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Preconditions;

final class NonLeaf implements Node {

	private final List<? extends Node> children;
	private final Rectangle mbr;

	NonLeaf(List<? extends Node> children) {
		Preconditions.checkArgument(!children.isEmpty());
		this.children = children;
		this.mbr = Util.mbr(children);
	}

	List<? extends Node> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public Node add(Entry entry, ImmutableStack<NonLeaf> stack) {
		final HasMbr child = Util.findLeastIncreaseInMbrArea(entry.mbr(),
				children);
		return ((Node) child).add(entry, stack.push(this));
	}

	@Override
	public void search(Rectangle r, Subscriber<? super Entry> subscriber) {
		for (Node child : children) {
			if (subscriber.isUnsubscribed())
				return;
			else {
				if (r.overlaps(child.mbr()))
					child.search(r, subscriber);
			}
		}
	}

	@Override
	public void entries(Subscriber<? super Entry> subscriber) {
		for (Node child : children)
			if (subscriber.isUnsubscribed())
				return;
			else
				child.entries(subscriber);
	}

	@Override
	public String toString() {
		return "NonLeaf [mbr=" + mbr + "]";
	}

	@Override
	public void nearest(Rectangle r, int k, Subscriber<? super Entry> subscriber) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nearest(Rectangle r, int k,
			Func2<Rectangle, Rectangle, Double> distanceFunction,
			Subscriber<? super Entry> subscriber) {
		// TODO Auto-generated method stub

	}

}
