package com.github.davidmoten.rtree.internal.util;

import static com.github.davidmoten.guavamini.Optional.of;

import java.util.Iterator;

import com.github.davidmoten.guavamini.Optional;

public final class ImmutableStack<T> implements Iterable<T> {
    private final Optional<T> head;
    private final Optional<ImmutableStack<T>> tail;

    private static ImmutableStack<?> EMPTY = new ImmutableStack<Object>();

    public ImmutableStack(final T head, final ImmutableStack<T> tail) {
        this(of(head), of(tail));
    }

    private ImmutableStack(Optional<T> head, Optional<ImmutableStack<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static <T> ImmutableStack<T> create(T t) {
        return new ImmutableStack<T>(of(t), of(ImmutableStack.<T> empty()));
    }

    public ImmutableStack() {
        this(Optional.<T> absent(), Optional.<ImmutableStack<T>> absent());
    }

    @SuppressWarnings("unchecked")
    public static <S> ImmutableStack<S> empty() {
        return (ImmutableStack<S>) EMPTY;
    }

    public boolean isEmpty() {
        return !head.isPresent();
    }

    public T peek() {
        // if (isEmpty())
        // throw new RuntimeException("cannot peek on empty stack");
        // else
        return this.head.get();
    }

    public ImmutableStack<T> pop() {
        // if (isEmpty())
        // throw new RuntimeException("cannot pop on empty stack");
        // else
        return this.tail.get();
    }

    public ImmutableStack<T> push(T value) {
        return new ImmutableStack<T>(value, this);
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator<T>(this);
    }

    private static class StackIterator<U> implements Iterator<U> {
        private ImmutableStack<U> stack;

        public StackIterator(final ImmutableStack<U> stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override
        public U next() {
            final U result = this.stack.peek();
            this.stack = this.stack.pop();
            return result;
        }

        @Override
        public void remove() {
            throw new RuntimeException("not supported");
        }
    }

}