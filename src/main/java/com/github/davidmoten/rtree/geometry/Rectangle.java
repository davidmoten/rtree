package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class Rectangle implements Geometry {
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
        return new Rectangle(Math.min(x1, r.x1), Math.min(y1, r.y1), Math.max(x2, r.x2), Math.max(
                y2, r.y2));
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

    private boolean intersects(Rectangle r) {
        return instersectsOnce(r) || r.instersectsOnce(this);
    }

    @Override
    public double distance(Rectangle r) {
        if (intersects(r))
            return 0;
        else {
            Rectangle mostLeft = x1 < r.x1 ? this : r;
            Rectangle mostRight = x1 > r.x1 ? this : r;
            double xDifference = Math.max(0, mostLeft.x1 == mostRight.x1 ? 0 : mostRight.x1
                    - mostLeft.x2);

            Rectangle upper = y1 < r.y1 ? this : r;
            Rectangle lower = y1 > r.y1 ? this : r;

            double yDifference = Math.max(0, upper.y1 == lower.y1 ? 0 : lower.y1 - upper.y2);

            return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        }
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public String toString() {
        return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Rectangle> other = ObjectsHelper.asClass(obj, Rectangle.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
        } else
            return false;
    }

    public float intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else
            return create(Math.max(x1, r.x1), Math.max(y1, r.y1), Math.min(x2, r.x2),
                    Math.min(y2, r.y2)).area();
    }

    public double perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

}
