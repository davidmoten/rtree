package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class Entry<T> implements HasGeometry {
	private final T value;
	private final Geometry geometry;

	public Entry(T value, Geometry geometry) {
		Preconditions.checkNotNull(geometry);
		this.value = value;
		this.geometry = geometry;
	}

	public static <T> Entry<T> entry(T value, Geometry geometry) {
		return new Entry<T>(value, geometry);
	}

	public T value() {
		return value;
	}

	@Override
	public Geometry geometry() {
		return geometry;
	}

	@Override
	public String toString() {
		return "Entry [value=" + value + ", geometry=" + geometry + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value, geometry);
	}

	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("rawtypes")
		Optional<Entry> other = ObjectsHelper.asClass(obj, Entry.class);
		if (other.isPresent()) {
			return Objects.equal(value, other.get().value)
					&& Objects.equal(geometry, other.get().geometry);
		} else
			return false;
	}

}
