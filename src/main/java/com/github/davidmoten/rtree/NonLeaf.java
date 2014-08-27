package com.github.davidmoten.rtree;

import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

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
	public void search(Func1<? super Rectangle, Boolean> criterion,
			Subscriber<? super Entry> subscriber) {
		for (Node child : children) {
			if (subscriber.isUnsubscribed())
				return;
			else {
				if (criterion.call(child.mbr()))
					child.search(criterion, subscriber);
			}
		}
	}

	@Override
	public String toString() {
		return "NonLeaf [mbr=" + mbr + "]";
	}

}
