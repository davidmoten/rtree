package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;

public class SplitterRStarTest {

    @Test
    public void testGetPairs() {

        int minSize = 2;
        List<HasGeometry> list = Lists.newArrayList();
        list.add(Geometries.point(1, 1).mbr());
        list.add(Geometries.point(2, 2).mbr());
        list.add(Geometries.point(3, 3).mbr());
        list.add(Geometries.point(4, 4).mbr());
        list.add(Geometries.point(5, 5).mbr());
        List<ListPair<HasGeometry>> pairs = SplitterRStar.getPairs(minSize, list);
        assertEquals(2, pairs.size());
    }
}
