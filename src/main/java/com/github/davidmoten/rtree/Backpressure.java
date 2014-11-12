package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

/**
 * Utility methods for controlling backpressure of the tree search.
 */
final class Backpressure {

    private Backpressure() {
        // prevent instantiation
    }

    static <T, S extends Geometry> ImmutableStack<NodePosition<T, S>> search(
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T, S>> subscriber,
            ImmutableStack<NodePosition<T, S>> stack, long request) {
        while (!stack.isEmpty()) {
            NodePosition<T, S> np = stack.peek();
            if (subscriber.isUnsubscribed())
                return ImmutableStack.empty();
            else if (request == 0)
                return stack;
            else if (np.position() == np.node().count()) {
                // handle after last in node
                stack = searchAfterLastInNode(stack);
            } else if (np.node() instanceof NonLeaf) {
                // handle non-leaf
                stack = searchNonLeaf(condition, stack, np);
            } else {
                // handle leaf
                long nextRequest = searchLeaf(condition, subscriber, request, np);
                stack = stack.pop().push(np.nextPosition());
                request = nextRequest;
            }
        }
        return stack;
    }

    private static <T, S extends Geometry> long searchLeaf(
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T, S>> subscriber, long request, NodePosition<T, S> np) {
        final long nextRequest;
        Entry<T, S> entry = ((Leaf<T, S>) np.node()).entries().get(np.position());
        if (condition.call(entry.geometry())) {
            subscriber.onNext(entry);
            nextRequest = request - 1;
        } else
            nextRequest = request;
        return nextRequest;
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchAfterLastInNode(
            ImmutableStack<NodePosition<T, S>> stack) {
        ImmutableStack<NodePosition<T, S>> stack2 = stack.pop();
        if (stack2.isEmpty())
            stack = stack2;
        else {
            NodePosition<T, S> previous = stack2.peek();
            stack = stack2.pop().push(previous.nextPosition());
        }
        return stack;
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchNonLeaf(
            final Func1<? super Geometry, Boolean> condition,
            ImmutableStack<NodePosition<T, S>> stack, NodePosition<T, S> np) {
        Node<T, S> child = ((NonLeaf<T, S>) np.node()).children().get(np.position());
        if (condition.call(child.geometry())) {
            stack = stack.push(new NodePosition<T, S>(child, 0));
        } else {
            stack = stack.pop().push(np.nextPosition());
        }
        return stack;
    }

}
