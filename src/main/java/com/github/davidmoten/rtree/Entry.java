package com.github.davidmoten.rtree;

import com.google.common.base.Preconditions;

public class Entry implements HasMbr {
	private final Object object;
	private final Geometry geometry;

	public Entry(Object object, Geometry geometry) {
		Preconditions.checkNotNull(object);
		Preconditions.checkNotNull(geometry);
		this.object = object;
		this.geometry = geometry;
	}

	public Entry(Object object, double x, double y) {
		this(object, Rectangle.create(x, y, x, y));
	}

	public Object object() {
		return object;
	}

	@Override
	public Rectangle mbr() {
		return geometry.mbr();
	}

	public Geometry geometry() {
		return geometry;
	}

	@Override
	public String toString() {
		return "Entry [object=" + object + ", geometry=" + geometry + "]";
	}

}
