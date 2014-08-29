package com.github.davidmoten.rtree;

import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

final class NonLeaf<T> implements Node<T> {

	private final List<? extends Node<T>> children;
	private final Rectangle mbr;
	private final Context context;

	NonLeaf(List<? extends Node<T>> children, Context context) {
		this.context = context;
		Preconditions.checkArgument(!children.isEmpty());
		this.children = children;
		this.mbr = Util.mbr(children);
	}

	List<? extends Node<T>> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public Node<T> add(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack) {
		final Node<T> child = context.selector().select(entry.mbr(), children);
		return child.add(entry, stack.push(this));
	}

	@Override
	public void search(Func1<? super Geometry, Boolean> criterion,
			Subscriber<? super Entry<T>> subscriber) {

		for (Node<T> child : children) {
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

	@Override
	public Optional<Node<T>> delete(Entry<T> entry,
			ImmutableStack<NonLeaf<T>> stack) {
		for (Node<T> child : children) {
			if (entry.geometry().intersects(child.mbr())) {
				Optional<Node<T>> result = child
						.delete(entry, stack.push(this));
				if (result.isPresent())
					return result;
			}
		}
		if (stack.isEmpty())
			return Optional.<Node<T>> of(this);
		else
			return Optional.absent();
	}

}
