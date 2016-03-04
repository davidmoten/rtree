package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.util.ImmutableStack;

import rx.Subscriber;
import rx.functions.Func1;

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
            final ImmutableStack<NodePosition<T, S>> stack, final long request) {
        StackAndRequest<NodePosition<T, S>> state = StackAndRequest.create(stack, request);
        return searchAndReturnStack(condition, subscriber, state);
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchAndReturnStack(
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T, S>> subscriber,
            StackAndRequest<NodePosition<T, S>> state) {

        while (!state.stack.isEmpty()) {
            NodePosition<T, S> np = state.stack.peek();
            if (subscriber.isUnsubscribed())
                return ImmutableStack.empty();
            else if (state.request <= 0)
                return state.stack;
            else if (np.position() == np.node().count()) {
                // handle after last in node
                state = StackAndRequest.create(searchAfterLastInNode(state.stack), state.request);
            } else if (np.node() instanceof NonLeaf) {
                // handle non-leaf
                state = StackAndRequest.create(searchNonLeaf(condition, state.stack, np),
                        state.request);
            } else {
                // handle leaf
                state = searchLeaf(condition, subscriber, state, np);
            }
        }
        return state.stack;
    }

    private static class StackAndRequest<T> {
        private final ImmutableStack<T> stack;
        private final long request;

        StackAndRequest(ImmutableStack<T> stack, long request) {
            this.stack = stack;
            this.request = request;
        }

        static <T> StackAndRequest<T> create(ImmutableStack<T> stack, long request) {
            return new StackAndRequest<T>(stack, request);
        }

    }

    private static <T, S extends Geometry> StackAndRequest<NodePosition<T, S>> searchLeaf(
            final Func1<? super Geometry, Boolean> condition,
            final Subscriber<? super Entry<T, S>> subscriber,
            StackAndRequest<NodePosition<T, S>> state, NodePosition<T, S> np) {
        final long nextRequest;
        Entry<T, S> entry = ((Leaf<T, S>) np.node()).entry(np.position());
        if (condition.call(entry.geometry())) {
            subscriber.onNext(entry);
            nextRequest = state.request - 1;
        } else
            nextRequest = state.request;
        return StackAndRequest.create(state.stack.pop().push(np.nextPosition()), nextRequest);
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchNonLeaf(
            final Func1<? super Geometry, Boolean> condition,
            ImmutableStack<NodePosition<T, S>> stack, NodePosition<T, S> np) {
        Node<T, S> child = ((NonLeaf<T, S>) np.node()).child(np.position());
        if (condition.call(child.geometry())) {
            stack = stack.push(new NodePosition<T, S>(child, 0));
        } else {
            stack = stack.pop().push(np.nextPosition());
        }
        return stack;
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

}
