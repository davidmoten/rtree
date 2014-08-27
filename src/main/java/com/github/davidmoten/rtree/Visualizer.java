package com.github.davidmoten.rtree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.common.base.Optional;

public class Visualizer {

	private final RTree tree;
	private final int width;
	private final int height;
	private final Rectangle view;
	private final int maxDepth;

	public Visualizer(RTree tree, int width, int height, Rectangle view) {
		this.tree = tree;
		this.width = width;
		this.height = height;
		this.view = view;
		this.maxDepth = calculateMaxDepth(tree.root());
	}

	private static int calculateMaxDepth(Optional<Node> root) {
		if (!root.isPresent())
			return 0;
		else
			return calculateDepth(root.get(), 0);
	}

	private static int calculateDepth(Node node, int depth) {
		if (node instanceof Leaf)
			return depth + 1;
		else
			return calculateDepth(((NonLeaf) node).children().get(0), depth + 1);
	}

	public BufferedImage create() {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, width, height);

		if (tree.root().isPresent()) {
			List<RectangleDepth> nodeDepths = getNodeDepthsSortedByDepth(tree
					.root().get());
			drawNode(g, nodeDepths);
		}
		return image;
	}

	private List<RectangleDepth> getNodeDepthsSortedByDepth(Node root) {
		List<RectangleDepth> list = getRectangleDepths(root, 0);
		Collections.sort(list, new Comparator<RectangleDepth>() {

			@Override
			public int compare(RectangleDepth n1, RectangleDepth n2) {
				return ((Integer) n1.getDepth()).compareTo(n2.getDepth());
			}
		});
		return list;
	}

	private List<RectangleDepth> getRectangleDepths(Node node, int depth) {
		List<RectangleDepth> list = new ArrayList<RectangleDepth>();
		list.add(new RectangleDepth(node.mbr(), depth));
		if (node instanceof Leaf) {
			Leaf leaf = (Leaf) node;
			for (Entry entry : leaf.entries()) {
				list.add(new RectangleDepth(entry.mbr(), depth + 2));
			}
		} else {
			NonLeaf n = (NonLeaf) node;
			for (Node child : n.children()) {
				list.addAll(getRectangleDepths(child, depth + 1));
			}
		}
		return list;
	}

	private void drawNode(Graphics2D g, List<RectangleDepth> nodes) {
		for (RectangleDepth node : nodes) {
			Color color = Color.getHSBColor(node.getDepth() / (maxDepth + 1f),
					1f, 1f);
			g.setStroke(new BasicStroke(
					2 * (maxDepth - node.getDepth() + 2) - 1));
			g.setColor(color);
			Rectangle r = node.getRectangle();
			drawRectangle(g, r);
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
