package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Preconditions;

class Backpressure {

    static <T> ImmutableStack<NodePosition<T>> search(Node<T> node,
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T>> subscriber, ImmutableStack<NodePosition<T>> stack,
            long request) {
        while (true) {
            Preconditions.checkArgument(!stack.isEmpty());
            NodePosition<T> np = stack.peek();
            Preconditions.checkArgument(node == np.node());
            if (subscriber.isUnsubscribed())
                return ImmutableStack.empty();
            else if (request == 0)
                return stack;
            else if (np.position() == node.count()) {
                ImmutableStack<NodePosition<T>> stack2 = stack.pop();
                if (stack2.isEmpty())
                    return stack2;
                else {
                    NodePosition<T> previous = stack2.peek();
                    node = previous.node();
                    stack = stack2.pop().push(previous.nextPosition());
                }
            } else if ((node instanceof NonLeaf)) {
                Node<T> child = ((NonLeaf<T>) node).children().get(np.position());
                if (condition.call(child.geometry())) {
                    node = child;
                    stack = stack.push(new NodePosition<T>(child, 0));
                } else {
                    stack = stack.pop().push(np.nextPosition());
                }
            } else {
                Entry<T> entry = ((Leaf<T>) node).entries().get(np.position());
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
