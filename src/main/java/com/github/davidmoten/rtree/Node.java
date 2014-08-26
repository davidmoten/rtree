package com.github.davidmoten.rtree;

import java.util.Optional;

public interface Node {

	Optional<NonLeaf> parent();

	Rectangle mbr();
}
