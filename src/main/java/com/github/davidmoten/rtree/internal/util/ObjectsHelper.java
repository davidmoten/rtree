package com.github.davidmoten.rtree.internal.util;

public final class ObjectsHelper {

    private ObjectsHelper() {
        // prevent instantiation
    }

    static void instantiateForTestCoveragePurposesOnly() {
        new ObjectsHelper();
    }

    @SuppressWarnings("unchecked")
    public static <T> com.github.davidmoten.guavamini.Optional<T> asClass(Object object,
            Class<T> cls) {
        if (object == null)
            return com.github.davidmoten.guavamini.Optional.absent();
        else if (object.getClass() != cls)
            return com.github.davidmoten.guavamini.Optional.absent();
        else
            return com.github.davidmoten.guavamini.Optional.of((T) object);
    }

}
