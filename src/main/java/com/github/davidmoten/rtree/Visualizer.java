package com.github.davidmoten.rtree;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Visualizer {

	private final RTree tree;
	private final int width;
	private final int height;
	private final Rectangle view;
	private final int maxDepth;

	public Visualizer(RTree tree, int width, int height, Rectangle view,
			int maxDepth) {
		this.tree = tree;
		this.width = width;
		this.height = height;
		this.view = view;
		this.maxDepth = maxDepth;
	}

	public BufferedImage create() {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, width, height);
		if (tree.root().isPresent())
			drawNode((Graphics2D) image.getGraphics(), tree.root().get(), 0);
		return image;
	}

	private void drawNode(Graphics2D g, Node node, int depth) {
		Color color = Color.getHSBColor(depth / (float) maxDepth, 1f, 1f);
		g.setColor(color);
		Rectangle r = node.mbr();
		drawRectangle(g, r);
		if (node instanceof Leaf) {
			g.setColor(Color.black);
			Leaf leaf = (Leaf) node;
			for (Entry entry : leaf.entries()) {
				drawRectangle(g, entry.mbr());
			}
		} else {
			NonLeaf n = (NonLeaf) node;
			for (Node child : n.children()) {
				drawNode(g, child, depth + 1);
			}
		}

	}

	private void drawRectangle(Graphics2D g, Rectangle r) {
		double x1 = (r.x1() - view.x1()) / (view.x2() - view.x1()) * width;
		double y1 = (r.y1() - view.y1()) / (view.y2() - view.y1()) * height;
		double x2 = (r.x2() - view.x1()) / (view.x2() - view.x1()) * width;
		double y2 = (r.y2() - view.y1()) / (view.y2() - view.y1()) * height;
		g.drawRect(rnd(x1), rnd(y1), rnd(x2 - x1), rnd(y2 - y1));
	}

	private static int rnd(double d) {
		return (int) Math.round(d);
	}

	public void save(File file, String imageFormat) {
		try {
			ImageIO.write(create(), imageFormat, file);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
