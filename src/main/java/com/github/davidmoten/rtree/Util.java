package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

final class Util {

    private Util() {
        // prevent instantiation
    }

    static void instantiateForTestCoveragePurposesOnly() {
        new Util();
    }

    static Rectangle mbr(Collection<? extends HasGeometry> items) {
        Preconditions.checkArgument(!items.isEmpty());
        Optional<Rectangle> r = Optional.absent();
        for (final HasGeometry item : items) {
            if (r.isPresent())
                r = of(r.get().add(item.geometry().mbr()));
            else
                r = of(item.geometry().mbr());
        }
        return r.get();
    }

    static <T> List<T> add(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.add(element);
        return result;
    }

    static <T> List<T> remove(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.remove(element);
        return result;
    }

    static <T> List<? extends T> replace(List<? extends T> list, T node,
            List<? extends T> replacements) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.remove(node);
        result.addAll(replacements);
        return result;
    }

}
