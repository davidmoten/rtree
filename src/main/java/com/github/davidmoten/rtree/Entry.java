package com.github.davidmoten.rtree;

import com.google.common.base.Preconditions;

public class Entry implements HasMbr {
	private final Object object;
	private final Rectangle mbr;

	public Entry(Object object, Rectangle mbr) {
		Preconditions.checkNotNull(object);
		Preconditions.checkNotNull(mbr);
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

	@Override
	public String toString() {
		return "Entry [object=" + object + ", mbr=" + mbr + "]";
	}

}
