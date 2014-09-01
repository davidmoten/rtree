package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class Point implements Geometry {

	private final Rectangle mbr;

	public Point(float x, float y) {
		this.mbr = Rectangle.create(x, y, x, y);
	}

	public static Point create(double x, double y) {
		return new Point((float) x, (float) y);
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public double distance(Rectangle r) {
		return mbr.distance(r);
	}

	public float x() {
		return mbr.x1();
	}

	public float y() {
		return mbr.y1();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(mbr);
	}

	@Override
	public boolean equals(Object obj) {
		Optional<Point> other = ObjectsHelper.asClass(obj, Point.class);
		if (other.isPresent()) {
			return Objects.equal(mbr, other.get().mbr());
		} else
			return false;
	}

}
