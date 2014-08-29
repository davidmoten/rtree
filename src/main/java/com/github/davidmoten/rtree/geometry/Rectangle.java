package com.github.davidmoten.rtree.geometry;

import com.google.common.base.Preconditions;

public class Rectangle implements Geometry {
	private final float x1, y1, x2, y2;

	public Rectangle(float x1, float y1, float x2, float y2) {
		Preconditions.checkArgument(x2 >= x1);
		Preconditions.checkArgument(y2 >= y1);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public float x1() {
		return x1;
	}

	public float y1() {
		return y1;
	}

	public float x2() {
		return x2;
	}

	public float y2() {
		return y2;
	}

	public float area() {
		return Math.abs((x1 - x2) * (y1 - y2));
	}

	public Rectangle add(Rectangle r) {
		return new Rectangle(Math.min(x1, r.x1), Math.min(y1, r.y1), Math.max(
				x2, r.x2), Math.max(y2, r.y2));
	}

	public static Rectangle create(double x1, double y1, double x2, double y2) {
		return new Rectangle((float) x1, (float) y1, (float) x2, (float) y2);
	}

	public static Rectangle create(float x1, float y1, float x2, float y2) {
		return new Rectangle(x1, y1, x2, y2);
	}

	public boolean in(double x, double y) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	private boolean instersectsOnce(Rectangle r) {
		return r.in(x1, y1) || r.in(x2, y2);
	}

	@Override
	public boolean intersects(Rectangle r) {
		return instersectsOnce(r) || r.instersectsOnce(this);
	}

	@Override
	public double distance(Rectangle r) {
		if (intersects(r))
			return 0;
		else {
			Rectangle mostLeft = x1 < r.x1 ? this : r;
			Rectangle mostRight = x1 > r.x1 ? this : r;
			double xDifference = Math.max(0, mostLeft.x1 == mostRight.x1 ? 0
					: mostRight.x1 - mostLeft.x2);

			Rectangle upper = y1 < r.y1 ? this : r;
			Rectangle lower = y1 > r.y1 ? this : r;

			double yDifference = Math.max(0, upper.y1 == lower.y1 ? 0
					: lower.y1 - upper.y2);

			return Math.sqrt(xDifference * xDifference + yDifference
					* yDifference);
		}
	}

	@Override
	public Rectangle mbr() {
		return this;
	}

	@Override
	public String toString() {
		return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2="
				+ y2 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x1);
		result = prime * result + Float.floatToIntBits(x2);
		result = prime * result + Float.floatToIntBits(y1);
		result = prime * result + Float.floatToIntBits(y2);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rectangle other = (Rectangle) obj;
		if (Float.floatToIntBits(x1) != Float.floatToIntBits(other.x1))
			return false;
		if (Float.floatToIntBits(x2) != Float.floatToIntBits(other.x2))
			return false;
		if (Float.floatToIntBits(y1) != Float.floatToIntBits(other.y1))
			return false;
		if (Float.floatToIntBits(y2) != Float.floatToIntBits(other.y2))
			return false;
		return true;
	}

}
