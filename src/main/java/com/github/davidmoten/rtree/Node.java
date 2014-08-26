package com.github.davidmoten.rtree;

import com.github.davidmoten.util.ImmutableStack;

public interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);
}
