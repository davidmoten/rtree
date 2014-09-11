package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

final class Backpressure {

    static <T> ImmutableStack<NodePosition<T>> search(
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T>> subscriber, ImmutableStack<NodePosition<T>> stack,
            long request) {
        if (stack.isEmpty())
            return stack;
        while (true) {
            NodePosition<T> np = stack.peek();
            if (subscriber.isUnsubscribed())
                return ImmutableStack.empty();
            else if (request == 0)
                return stack;
            else if (np.position() == np.node().count()) {
                // handle after last position in node
                ImmutableStack<NodePosition<T>> stack2 = stack.pop();
                if (stack2.isEmpty())
                    return stack2;
                else {
                    NodePosition<T> previous = stack2.peek();
                    stack = stack2.pop().push(previous.nextPosition());
                }
            } else if ((np.node() instanceof NonLeaf)) {
                // handle non-leaf
                Node<T> child = ((NonLeaf<T>) np.node()).children().get(np.position());
                if (condition.call(child.geometry())) {
                    stack = stack.push(new NodePosition<T>(child, 0));
                } else {
                    stack = stack.pop().push(np.nextPosition());
                }
            } else {
                // handle leaf
                Entry<T> entry = ((Leaf<T>) np.node()).entries().get(np.position());
                final long nextRequest;
                if (condition.call(entry.geometry())) {
                    subscriber.onNext(entry);
                    nextRequest = request - 1;
                } else
                    nextRequest = request;
                stack = stack.pop().push(np.nextPosition());
                request = nextRequest;
            }
        }
    }

}
