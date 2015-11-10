package com.github.davidmoten.rtree.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A line segment.
 */
public class Line implements Geometry {

    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;

    private Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static Line create(float x1, float y1, float x2, float y2) {
        return new Line(x1, y1, x2, y2);
    }

    @Override
    public double distance(Rectangle r) {
        return 0;
    }

    @Override
    public Rectangle mbr() {
        return Geometries.rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2),
                Math.max(y1, y2));
    }

    @Override
    public boolean intersects(Rectangle r) {
        Line2D line = new Line2D.Float(x1, y1, x2, y2);
        Rectangle2D rect = new Rectangle2D.Float(r.x1(), r.y1(), r.x2(), r.y2());
        return line.intersects(rect);
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

    public Boolean intersects(Line b) {
        Line2D line1 = new Line2D.Float(x1, y1, x2, y2);
        Line2D line2 = new Line2D.Float(b.x1(), b.y1(), b.x2(), b.y2());
        return line2.intersectsLine(line1);
    }

}
