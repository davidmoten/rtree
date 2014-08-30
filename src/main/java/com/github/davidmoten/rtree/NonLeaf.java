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
        Preconditions.checkArgument(!children.isEmpty());
        this.context = context;
        this.children = children;
        this.mbr = Util.mbr(children);
    }

    List<? extends Node<T>> children() {
        return children;
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public Node<T> add(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack) {
        final Node<T> child = context.selector().select(entry.geometry().mbr(), children);
        return child.add(entry, stack.push(this));
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T>> subscriber) {

        for (final Node<T> child : children) {
            if (subscriber.isUnsubscribed())
                return;
            else {
                if (criterion.call(child.geometry().mbr()))
                    child.search(criterion, subscriber);
            }
        }
    }

    @Override
    public String toString() {
        return "NonLeaf [mbr=" + mbr + "]";
    }

    @Override
    public Optional<Node<T>> delete(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack) {
        for (final Node<T> child : children) {
            if (entry.geometry().intersects(child.geometry().mbr())) {
                final Optional<Node<T>> result = child.delete(entry, stack.push(this));
                if (result.isPresent())
                    return result;
            }
        }
        if (stack.isEmpty())
            return Optional.<Node<T>> of(this);
        else
            return Optional.absent();
    }

    @Override
    public ImmutableStack<NodePosition<T>> search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T>> subscriber, ImmutableStack<NodePosition<T>> stack,
            long request) {
        Preconditions.checkArgument(!stack.isEmpty());
        NodePosition<T> np = stack.peek();
        Preconditions.checkArgument(this == np.node());
        Preconditions.checkArgument(np.position() <= children.size());
        if (request == 0)
            return stack;
        if (np.position() == children.size()) {
            ImmutableStack<NodePosition<T>> stack2 = stack.pop();
            if (stack2.isEmpty())
                return stack2;
            else {
                NodePosition<T> previous = stack2.peek();
                return previous.node().search(condition, subscriber,
                        stack2.pop().push(previous.nextPosition()), request);
            }
        } else {
            Node<T> child = children.get(np.position());
            if (condition.call(child.geometry()))
                return child.search(condition, subscriber,
                        stack.push(new NodePosition<T>(child, 0)), request);
            else
                return search(condition, subscriber, stack.pop().push(np.nextPosition()), request);
        }
    }
}
