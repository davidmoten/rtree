package com.github.davidmoten.rtree.geometry.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.Line2D;
import com.github.davidmoten.rtree.internal.RectangleUtil;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

/**
 * A line segment.
 */
public final class LineFloat implements Line {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    private LineFloat(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static LineFloat create(double x1, double y1, double x2, double y2) {
        return new LineFloat(x1, y1, x2, y2);
    }

    @Override
    public double distance(Rectangle r) {
        if (r.contains(x1, y1) || r.contains(x2, y2)) {
            return 0;
        } else {
            double d1 = distance(r.x1(), r.y1(), r.x1(), r.y2());
            if (d1 == 0)
                return 0;
            double d2 = distance(r.x1(), r.y2(), r.x2(), r.y2());
            if (d2 == 0)
                return 0;
            double d3 = distance(r.x2(), r.y2(), r.x2(), r.y1());
            double d4 = distance(r.x2(), r.y1(), r.x1(), r.y1());
            return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        Line2D line = new Line2D(x1, y1, x2, y2);
        double d1 = line.ptSegDist(this.x1, this.y1);
        double d2 = line.ptSegDist(this.x2, this.y2);
        Line2D line2 = new Line2D(this.x1, this.y1, this.x2, this.y2);
        double d3 = line2.ptSegDist(x1, y1);
        if (d3 == 0)
            return 0;
        double d4 = line2.ptSegDist(x2, y2);
        if (d4 == 0)
            return 0;
        else
            return Math.min(d1, Math.min(d2, Math.min(d3, d4)));

    }

    @Override
    public Rectangle mbr() {
        return Geometries.rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2),
                Math.max(y1, y2));
    }

    @Override
    public boolean intersects(Rectangle r) {
        return RectangleUtil.rectangleIntersectsLine(r.x1(), r.y1(), r.x2() - r.x1(),
                r.y2() - r.y1(), x1, y1, x2, y2);
    }

    @Override
    public double x1() {
        return x1;
    }

    @Override
    public double y1() {
        return y1;
    }

    @Override
    public double x2() {
        return x2;
    }

    @Override
    public double y2() {
        return y2;
    }

    @Override
    public boolean intersects(Line b) {
        Line2D line1 = new Line2D(x1, y1, x2, y2);
        Line2D line2 = new Line2D(b.x1(), b.y1(), b.x2(), b.y2());
        return line2.intersectsLine(line1);
    }

    @Override
    public boolean intersects(Point point) {
        return intersects(point.mbr());
    }

    @Override
    public boolean intersects(Circle circle) {
        return GeometryUtil.lineIntersects(x1, y1, x2, y2, circle);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<LineFloat> other = ObjectsHelper.asClass(obj, LineFloat.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
        } else
            return false;
    }

    @Override
    public boolean isDoublePrecision() {
        return false;
    }

}
