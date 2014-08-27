package com.github.davidmoten.rtree;

final class RectangleDepth {
	private final Rectangle rectangle;
	private final int depth;

	RectangleDepth(Rectangle rectangle, int depth) {
		super();
		this.rectangle = rectangle;
		this.depth = depth;
	}

	Rectangle getRectangle() {
		return rectangle;
	}

	int getDepth() {
		return depth;
	}

}
