package com.github.davidmoten.rtree;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.base.Optional;

public class Visualizer {

	private final RTree tree;
	private final int width;
	private final int height;

	public Visualizer(RTree tree, int width, int height, Rectangle view) {
		this.tree = tree;
		this.width = width;
		this.height = height;
	}

	public BufferedImage create() {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) image.getGraphics();
		if (tree.root().isPresent())
			drawNode(image, tree.root(), 0);
		return image;
	}

	private void drawNode(BufferedImage image, Optional<Node> node, int depth) {

	}

	public void save(File file, String imageFormat) {
		try {
			ImageIO.write(create(), imageFormat, file);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
