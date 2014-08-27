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

	public static <T> List<? extends T> replace(List<? extends T> list, T node,
			List<? extends T> replacements) {
		final ArrayList<T> result = new ArrayList<T>(list);
		result.remove(node);
		result.addAll(replacements);
		return result;
	}
}
