package com.github.davidmoten.rtree.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A line segment.
 */
public final class Line implements Geometry {

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
        if (x1 >= r.x1() && x2 <= r.x2() && y1 >= r.y1() && y2 <= r.y2())
            return 0;
        else {
            Line2D line1 = new Line2D.Float(r.x1(), r.y1(), r.x1(), r.y2());
            double d1 = distance(line1);
            Line2D line2 = new Line2D.Float(r.x1(), r.y2(), r.x2(), r.y2());
            double d2 = distance(line2);
            Line2D line3 = new Line2D.Float(r.x2(), r.y2(), r.x2(), r.y1());
            double d3 = distance(line3);
            Line2D line4 = new Line2D.Float(r.x2(), r.y1(), r.x1(), r.y1());
            double d4 = distance(line4);
            return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
        }
    }

    private double distance(Line2D line) {
        double d1 = line.ptSegDist(x1, y1);
        double d2 = line.ptSegDist(x2, y2);
        if (d1 < d2)
            return d1;
        else
            return d2;
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

    public boolean intersects(Line b) {
        Line2D line1 = new Line2D.Float(x1, y1, x2, y2);
        Line2D line2 = new Line2D.Float(b.x1(), b.y1(), b.x2(), b.y2());
        return line2.intersectsLine(line1);
    }

    public boolean intersects(Circle circle) {
        Vector c = Vector.create(circle.x(), circle.y());
        Vector a = Vector.create(x1, y1);
        Vector cMinusA = c.minus(a);
        float radiusSquared = circle.radius() * circle.radius();
        if (x1 == x2 && y1 == y2) {
            return cMinusA.modulusSquared() <= radiusSquared;
        } else {
            Vector b = Vector.create(x2, y2);
            Vector bMinusA = b.minus(a);
            float bMinusAModulus = bMinusA.modulus();
            float lambda = cMinusA.dot(bMinusA) / bMinusAModulus;
            if (lambda >= 0 && lambda <= bMinusAModulus) {
                Vector dMinusA = bMinusA.times(lambda / bMinusAModulus);
                return cMinusA.modulusSquared() - dMinusA.modulusSquared() <= radiusSquared;
            } else {
                // test if endpoint are within radius of centre
                return cMinusA.modulusSquared() <= radiusSquared
                        || c.minus(b).modulusSquared() <= radiusSquared;
            }
        }
    }

    private static class Vector {
        final float x;
        final float y;

        static Vector create(float x, float y) {
            return new Vector(x, y);
        }

        Vector(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float dot(Vector v) {
            return x * v.x + y * v.y;
        }

        Vector times(float value) {
            return create(value * x, value * y);
        }

        Vector minus(Vector v) {
            return create(x - v.x, y - v.y);
        }

        float modulus() {
            return (float) Math.sqrt(x * x + y * y);
        }

        float modulusSquared() {
            return x * x + y * y;
        }

        @Override
        public String toString() {
            return "Vector [x=" + x + ", y=" + y + "]";
        }

    }

}
