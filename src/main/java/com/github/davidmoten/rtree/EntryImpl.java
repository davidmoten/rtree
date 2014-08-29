package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Preconditions;

public class EntryImpl<T> implements Entry<T> {
    private final T object;
    private final Geometry geometry;

    public EntryImpl(T object, Geometry geometry) {
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(geometry);
        this.object = object;
        this.geometry = geometry;
    }

    public EntryImpl(T object, double x, double y) {
        this(object, Rectangle.create(x, y, x, y));
    }

    @Override
    public T object() {
        return object;
    }

    @Override
    public Geometry geometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return "Entry [object=" + object + ", geometry=" + geometry + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
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
        if (object == null) {
            if (other.object() != null)
                return false;
        } else if (!object.equals(other.object()))
            return false;
        return true;
    }

}
