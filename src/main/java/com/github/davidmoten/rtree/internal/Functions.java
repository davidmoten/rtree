package com.github.davidmoten.rtree.internal;

import rx.functions.Func1;

public final class Functions {

    private Functions() {
        // prevent instantiation
    }
    
    public static <T> Func1<T, T> identity() {
        return new Func1<T, T>() {
            @Override
            public T call(T t) {
                return t;
            }
        };
    }

    public static <T> Func1<T, Boolean> alwaysTrue() {
        return new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return true;
            }
        };
    }

    public static <T> Func1<T, Boolean> alwaysFalse() {
        return new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return false;
            }
        };
    }
    
}
