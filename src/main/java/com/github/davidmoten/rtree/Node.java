package com.github.davidmoten.rtree;

import java.util.Stack;

public interface Node {

	Rectangle mbr();

	NonLeaf add(Entry entry, Stack<NonLeaf> stack);
}
