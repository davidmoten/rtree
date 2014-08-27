package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.davidmoten.util.Pair;

public class QuadraticSplitterTest {

	@Test
	public void testWorstCombinationOn3() {
		Mbr r1 = r(1);
		Mbr r2 = r(100);
		Mbr r3 = r(3);
		Pair<Mbr> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1,
				r2, r3));
		assertEquals(r1, pair.value1());
		assertEquals(r2, pair.value2());
	}

	@Test
	public void testWorstCombinationOnTwoEntries() {
		Mbr r1 = r(1);
		Mbr r2 = r(2);
		Pair<Mbr> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1,
				r2));
		assertEquals(r1, pair.value1());
		assertEquals(r2, pair.value2());
	}

	@Test
	public void testWorstCombinationOn4() {
		Mbr r1 = r(2);
		Mbr r2 = r(1);
		Mbr r3 = r(3);
		Mbr r4 = r(4);
		Pair<Mbr> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1,
				r2, r3, r4));
		assertEquals(r2, pair.value1());
		assertEquals(r4, pair.value2());
	}

	@Test
	public void testGetBestCandidateForGroup1() {
		Mbr r1 = r(1);
		Mbr r2 = r(2);
		List<Mbr> list = Arrays.asList(r1);
		List<Mbr> group = Arrays.asList(r2);
		Mbr r = QuadraticSplitter.getBestCandidateForGroup(list, group,
				Util.mbr(group));
		assertEquals(r1, r);
	}

	@Test
	public void testGetBestCandidateForGroup2() {
		Mbr r1 = r(1);
		Mbr r2 = r(2);
		Mbr r3 = r(10);
		List<Mbr> list = Arrays.asList(r1);
		List<Mbr> group = Arrays.asList(r2, r3);
		Mbr r = QuadraticSplitter.getBestCandidateForGroup(list, group,
				Util.mbr(group));
		assertEquals(r1, r);
	}

	@Test
	public void testGetBestCandidateForGroup3() {
		Mbr r1 = r(1);
		Mbr r2 = r(2);
		Mbr r3 = r(10);
		List<Mbr> list = Arrays.asList(r1, r2);
		List<Mbr> group = Arrays.asList(r3);
		Mbr r = QuadraticSplitter.getBestCandidateForGroup(list, group,
				Util.mbr(group));
		assertEquals(r2, r);
	}

	private static Mbr r(int n) {
		return new Mbr(Rectangle.create(n, n, n + 1, n + 1));
	}

}
