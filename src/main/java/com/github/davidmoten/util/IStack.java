package com.github.davidmoten.util;

public interface IStack<T> extends Iterable<T> {
	IStack<T> push(T value);

	IStack<T> pop();

	T peek();

	boolean isEmpty();
}