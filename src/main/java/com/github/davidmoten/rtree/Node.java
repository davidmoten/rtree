package com.github.davidmoten.rtree;

import java.util.Stack;

public interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	NonLeaf add(Entry entry, Stack<NonLeaf> stack);
}
