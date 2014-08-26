package com.github.davidmoten.rtree;

import com.github.davidmoten.util.ImmutableStack;

public interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	NonLeaf add(Entry entry, ImmutableStack<NonLeaf> stack);
}
