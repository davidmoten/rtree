package com.github.davidmoten.rtree.geometry;

import java.util.List;

public final class ListPair<T extends HasGeometry> {
    private final Group<T> group1;
    private final Group<T> group2;

    public ListPair(List<T> list1, List<T> list2) {
        this.group1 = new Group<T>(list1);
        this.group2 = new Group<T>(list2);
    }

    public Group<T> group1() {
        return group1;
    }

    public Group<T> group2() {
        return group2;
    }

    public float areaSum() {
        return group1.geometry().mbr().area() + group2.geometry().mbr().area();
    }

    public float marginSum() {
        return group1.geometry().mbr().perimeter() + group2.geometry().mbr().perimeter();
    }

}
