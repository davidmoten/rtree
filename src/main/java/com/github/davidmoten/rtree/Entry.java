package com.github.davidmoten.rtree;

public class Entry implements HasMbr {
	private final Object object;
	private final Rectangle mbr;

	public Entry(Object object, Rectangle mbr) {
		this.object = object;
		this.mbr = mbr;
	}

	public Object object() {
		return object;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

}
