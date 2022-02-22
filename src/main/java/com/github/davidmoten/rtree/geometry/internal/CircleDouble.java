package com.github.davidmoten.rtree.geometry.internal;

import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

import java.util.Objects;
import java.util.Optional;

public final class CircleDouble implements Circle {

    private final double x, y, radius;
    private final Rectangle mbr;

    private CircleDouble(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = RectangleDouble.create(x - radius, y - radius, x + radius, y + radius);
    }

    public static CircleDouble create(double x, double y, double radius) {
        return new CircleDouble(x, y, radius);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double radius() {
        return radius;
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, GeometryUtil.distance(x, y, r) - radius);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return distance(r) == 0;
    }

    @Override
    public boolean intersects(Circle c) {
        double total = radius + c.radius();
        return GeometryUtil.distanceSquared(x, y, c.x(), c.y()) <= total * total;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, radius);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<CircleDouble> other = ObjectsHelper.asClass(obj, CircleDouble.class);
        if (other.isPresent()) {
            return Objects.equals(x, other.get().x) && Objects.equals(y, other.get().y)
                    && Objects.equals(radius, other.get().radius);
        } else
            return false;
    }

    @Override
    public boolean intersects(Point point) {
        return Math.sqrt(sqr(x - point.x()) + sqr(y - point.y())) <= radius;
    }

    private double sqr(double x) {
        return x * x;
    }

    @Override
    public boolean intersects(Line line) {
        return line.intersects(this);
    }

    @Override
    public boolean isDoublePrecision() {
        return true;
    }
}
