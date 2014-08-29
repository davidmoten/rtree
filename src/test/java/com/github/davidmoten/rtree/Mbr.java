package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class Mbr implements HasGeometry {

    private final Rectangle r;

    public Mbr(Rectangle r) {
        this.r = r;
    }

    @Override
    public Geometry geometry() {
        return r;
    }

}
