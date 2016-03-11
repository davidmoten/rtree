package com.github.davidmoten.rtree.internal.util;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class ObjectsHelper {

    private ObjectsHelper() {
        // prevent instantiation
    }

    @VisibleForTesting
    static void instantiateForTestCoveragePurposesOnly() {
        new ObjectsHelper();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> asClass(Object object, Class<T> cls) {
        if (object == null)
            return Optional.absent();
        else if (object.getClass() != cls)
            return Optional.absent();
        else
            return Optional.of((T) object);
    }

}
