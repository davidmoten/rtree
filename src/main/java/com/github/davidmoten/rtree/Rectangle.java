package com.github.davidmoten.rtree;

public class Rectangle {
	private final double x1, y1, x2, y2;

	public Rectangle(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public double x1() {
		return x1;
	}

	public double y1() {
		return y1;
	}

	public double x2() {
		return x2;
	}

	public double y2() {
		return y2;
	}

	public double area() {
		return Math.abs(x1 - x2) * Math.abs(y1 - y2);
	}

	public Rectangle add(Rectangle r) {
		return new Rectangle(Math.min(x1, r.x1), Math.min(y2, r.y2), Math.max(
				x2, r.x2), Math.max(y2, r.y2));
	}

	public static Rectangle create(double x1, double y1, double x2, double y2) {
		return new Rectangle(x1, y1, x2, y2);
	}
}
