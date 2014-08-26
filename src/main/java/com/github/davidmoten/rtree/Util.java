package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {

	public static Rectangle mbr(Collection<? extends HasMbr> items) {
		// TODO
		return null;
	}

	public static <T> List<T> add(List<T> list, T element) {
		final ArrayList<T> result = new ArrayList<T>(list);
		result.add(element);
		return result;
	}

	public static <T> List<T> replace(List<T> list, T value, T replacement) {
		final ArrayList<T> result = new ArrayList<T>(list);
		result.remove(value);
		result.add(replacement);
		return result;
	}
}
