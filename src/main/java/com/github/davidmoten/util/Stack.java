package com.github.davidmoten.util;

import java.util.Iterator;

class Stack<T> implements IStack<T> {
	private final T head;
	private final IStack<T> tail;

	public Stack(final T head, final IStack<T> tail) {
		this.head = head;
		this.tail = tail;
	}

	public static <U> IStack<U> empty(final Class<U> type) {
		return new EmptyStack<U>();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public T peek() {
		return this.head;
	}

	@Override
	public IStack<T> pop() {
		return this.tail;
	}

	@Override
	public IStack<T> push(T value) {
		return new Stack<T>(value, this);
	}

	@Override
	public Iterator<T> iterator() {
		return new StackIterator<T>(this);
	}

	private static class StackIterator<U> implements Iterator<U> {
		private IStack<U> stack;

		public StackIterator(final IStack<U> stack) {
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

	private static class EmptyStack<U> implements IStack<U> {

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public IStack<U> push(U value) {
			return new Stack<U>(value, this);
		}

		@Override
		public Iterator<U> iterator() {
			return new StackIterator<U>(this);
		}

		@Override
		public IStack<U> pop() {
			throw new RuntimeException("empty stack");
		}

		@Override
		public U peek() {
			throw new RuntimeException("empty stack");
		}
	}
}