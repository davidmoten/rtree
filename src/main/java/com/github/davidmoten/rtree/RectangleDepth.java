package com.github.davidmoten.rtree;

public class RectangleDepth {
	private final Rectangle rectangle;
	private final int depth;

	public RectangleDepth(Rectangle rectangle, int depth) {
		super();
		this.rectangle = rectangle;
		this.depth = depth;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public int getDepth() {
		return depth;
	}

}
