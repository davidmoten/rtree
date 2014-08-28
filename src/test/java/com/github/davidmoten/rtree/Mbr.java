package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.HasMbr;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class Mbr implements HasMbr {

	private final Rectangle r;

	public Mbr(Rectangle r) {
		this.r = r;
	}

	@Override
	public Rectangle mbr() {
		return r;
	}

}
