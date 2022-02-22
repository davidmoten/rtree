package com.github.davidmoten.rtree.internal;

import rx.functions.Func1;

public final class Functions {

    private Functions() {
        // prevent instantiation
    }
    
    public static <T> Func1<T, T> identity() {
        return t -> t;
    }

    public static <T> Func1<T, Boolean> alwaysTrue() {
        return t -> true;
    }

    public static <T> Func1<T, Boolean> alwaysFalse() {
        return t -> false;
    }
    
}
