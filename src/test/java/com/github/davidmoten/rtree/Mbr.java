package com.github.davidmoten.rtree;

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
