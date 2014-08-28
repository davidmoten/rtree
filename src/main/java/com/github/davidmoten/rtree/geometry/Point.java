package com.github.davidmoten.rtree.geometry;


public class Point implements Geometry {

	private final Rectangle mbr;

	public Point(float x, float y) {
		this.mbr = Rectangle.create(x, y, x, y);
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public double distance(Rectangle r) {
		return mbr.distance(r);
	}

	@Override
	public boolean intersects(Rectangle r) {
		return mbr.intersects(r);
	}

	public float x() {
		return mbr.x1();
	}

	public float y() {
		return mbr.y1();
	}

}
