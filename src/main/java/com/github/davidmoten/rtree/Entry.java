package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An entry in the R-tree which has a spatial representation.
 *
 * @param <T>
 *            the type of Entry
 */
public final class Entry<T> implements HasGeometry {
    private final T value;
    private final Geometry geometry;

    /**
     * Constructor.
     * 
     * @param value
     * @param geometry
     */
    public Entry(T value, Geometry geometry) {
        Preconditions.checkNotNull(geometry);
        this.value = value;
        this.geometry = geometry;
    }

    /**
     * Factory method.
     * 
     * @param value
     * @param geometry
     * @return
     */
    public static <T> Entry<T> entry(T value, Geometry geometry) {
        return new Entry<T>(value, geometry);
    }

    /**
     * Returns the value wrapped by this {@link Entry}.
     * 
     * @return
     */
    public T value() {
        return value;
    }

    @Override
    public Geometry geometry() {
        return geometry;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Entry [value=");
        builder.append(value);
        builder.append(", geometry=");
        builder.append(geometry);
        builder.append("]");
        return builder.toString();
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
