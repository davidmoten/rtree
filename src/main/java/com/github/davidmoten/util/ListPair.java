package com.github.davidmoten.util;

import java.util.List;

public final class ListPair<T> {
    private final List<T> list1;
    private final List<T> list2;

    public ListPair(List<T> list1, List<T> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public List<T> list1() {
        return list1;
    }

    public List<T> list2() {
        return list2;
    }

}
