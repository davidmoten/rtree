package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.google.common.base.Preconditions;

public final class Entry<T> implements HasGeometry {
    private final T value;
    private final Geometry geometry;

    public Entry(T value, Geometry geometry) {
        Preconditions.checkNotNull(value);
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Entry<?> other = (Entry<?>) obj;
        if (geometry == null) {
            if (other.geometry() != null)
                return false;
        } else if (!geometry.equals(other.geometry()))
            return false;
        if (value == null) {
            if (other.value() != null)
                return false;
        } else if (!value.equals(other.value()))
            return false;
        return true;
    }

}
