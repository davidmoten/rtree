package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.github.davidmoten.guavamini.Sets;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.internal.Util;
import com.github.davidmoten.rtree.internal.util.Pair;

public class QuadraticSplitterTest {

    @Test
    public void testWorstCombinationOn3() {
        final Mbr r1 = r(1);
        final Mbr r2 = r(100);
        final Mbr r3 = r(3);
        final Pair<Mbr> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2, r3));
        assertEquals(r1, pair.value1());
        assertEquals(r2, pair.value2());
    }

    @Test
    public void testWorstCombinationOnTwoEntries() {
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Pair<Mbr> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2));
        assertEquals(r1, pair.value1());
        assertEquals(r2, pair.value2());
    }

    @Test
    public void testWorstCombinationOn4() {
        final Mbr r1 = r(2);
        final Mbr r2 = r(1);
        final Mbr r3 = r(3);
        final Mbr r4 = r(4);
        final Pair<Mbr> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2, r3, r4));
        assertEquals(r2, pair.value1());
        assertEquals(r4, pair.value2());
    }

    @Test
    public void testGetBestCandidateForGroup1() {
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final List<Mbr> list = Collections.singletonList(r1);
        final List<Mbr> group = Collections.singletonList(r2);
        final Mbr r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbr(group));
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup2() {
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Mbr r3 = r(10);
        final List<Mbr> list = Collections.singletonList(r1);
        final List<Mbr> group = Arrays.asList(r2, r3);
        final Mbr r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbr(group));
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup3() {
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Mbr r3 = r(10);
        final List<Mbr> list = Arrays.asList(r1, r2);
        final List<Mbr> group = Arrays.asList(r3);
        final Mbr r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbr(group));
        assertEquals(r2, r);
    }

    @Test
    public void testSplit() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Mbr r3 = r(100);
        final Mbr r4 = r(101);
        final ListPair<Mbr> pair = q.split(Arrays.asList(r1, r2, r3, r4), 2);
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.group1().list()));
        assertEquals(Sets.newHashSet(r3, r4), Sets.newHashSet(pair.group2().list()));
    }

    @Test
    public void testSplit2() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Mbr r3 = r(100);
        final Mbr r4 = r(101);
        final Mbr r5 = r(103);
        final ListPair<Mbr> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5), 2);
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.group1().list()));
        assertEquals(Sets.newHashSet(r3, r4, r5), Sets.newHashSet(pair.group2().list()));
    }

    @Test
    public void testSplit3() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final Mbr r1 = r(1);
        final Mbr r2 = r(2);
        final Mbr r3 = r(100);
        final Mbr r4 = r(101);
        final Mbr r5 = r(103);
        final Mbr r6 = r(104);
        final ListPair<Mbr> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5, r6), 3);
        assertEquals(Sets.newHashSet(r1, r2, r3), Sets.newHashSet(pair.group1().list()));
        assertEquals(Sets.newHashSet(r4, r5, r6), Sets.newHashSet(pair.group2().list()));
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionForSplitEmptyList() {
        final SplitterQuadratic q = new SplitterQuadratic();
        q.split(Collections.<HasGeometry> emptyList(), 3);
    }

    private static Mbr r(int n) {
        return new Mbr(Geometries.rectangle(n, n, n + 1, n + 1));
    }

}
