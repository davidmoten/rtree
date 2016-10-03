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
        list.add(Geometries.point(new float[]{1f, 1f}).mbr());
        list.add(Geometries.point(new float[]{2f, 2f}).mbr());
        list.add(Geometries.point(new float[]{3f, 3f}).mbr());
        list.add(Geometries.point(new float[]{4f, 4f}).mbr());
        list.add(Geometries.point(new float[]{5f, 5f}).mbr());
        List<ListPair<HasGeometry>> pairs = SplitterRStar.getPairs(minSize, list);
        assertEquals(2, pairs.size());
    }
}
