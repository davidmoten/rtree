package com.github.davidmoten.rtree.internal;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public final class Functions {

    private Functions() {
        // prevent instantiation
    }
    
    public static <T> Function<T, T> identity() {
        return new Function<T, T>() {
            @Override
            public T apply(T t) {
                return t;
            }
        };
    }

    public static <T> Predicate<T> alwaysTrue() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return true;
            }
        };
    }

    public static <T> Predicate<T> alwaysFalse() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return false;
            }
        };
    }
    
}
