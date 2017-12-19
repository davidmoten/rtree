package com.github.davidmoten.rtree.internal;

/**
 * Port of java.awt.geom.Line2D. Created because Android does not support
 * java.awt.*.
 */
public final class Line2D {

    private final double x1, x2, y1, y2;

    public Line2D(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    /**
     * Returns the distance from a point to this line segment. The distance measured
     * is the distance between the specified point and the closest point between the
     * current line's end points. If the specified point intersects the line segment
     * in between the end points, this method returns 0.0.
     *
     * @param px
     *            the X coordinate of the specified point being measured against
     *            this line segment
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            this line segment
     * @return a double value that is the distance from the specified point to the
     *         current line segment.
     * @since 1.2
     */
    public double ptSegDist(double px, double py) {
        return ptSegDist(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    /**
     * Returns the distance from a point to a line segment. The distance measured is
     * the distance between the specified point and the closest point between the
     * specified end points. If the specified point intersects the line segment in
     * between the end points, this method returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line segment
     * @param y1
     *            the Y coordinate of the start point of the specified line segment
     * @param x2
     *            the X coordinate of the end point of the specified line segment
     * @param y2
     *            the Y coordinate of the end point of the specified line segment
     * @param px
     *            the X coordinate of the specified point being measured against the
     *            specified line segment
     * @param py
     *            the Y coordinate of the specified point being measured against the
     *            specified line segment
     * @return a double value that is the distance from the specified point to the
     *         specified line segment.
     * @since 1.2
     */
    public static double ptSegDist(double x1, double y1, double x2, double y2, double px,
            double py) {
        return Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
    }

    /**
     * Returns the square of the distance from a point to a line segment. The
     * distance measured is the distance between the specified point and the closest
     * point between the specified end points. If the specified point intersects the
     * line segment in between the end points, this method returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line segment
     * @param y1
     *            the Y coordinate of the start point of the specified line segment
     * @param x2
     *            the X coordinate of the end point of the specified line segment
     * @param y2
     *            the Y coordinate of the end point of the specified line segment
     * @param px
     *            the X coordinate of the specified point being measured against the
     *            specified line segment
     * @param py
     *            the Y coordinate of the specified point being measured against the
     *            specified line segment
     * @return a double value that is the square of the distance from the specified
     *         point to the specified line segment.
     * @since 1.2
     */
    public static double ptSegDistSq(double x1, double y1, double x2, double y2, double px,
            double py) {
        // Adjust vectors relative to x1,y1
        // x2,y2 becomes relative vector from x1,y1 to end of segment
        x2 -= x1;
        y2 -= y1;
        // px,py becomes relative vector from x1,y1 to test point
        px -= x1;
        py -= y1;
        double dotprod = px * x2 + py * y2;
        double projlenSq;
        if (dotprod <= 0.0) {
            // px,py is on the side of x1,y1 away from x2,y2
            // distance to segment is length of px,py vector
            // "length of its (clipped) projection" is now 0.0
            projlenSq = 0.0;
        } else {
            // switch to backwards vectors relative to x2,y2
            // x2,y2 are already the negative of x1,y1=>x2,y2
            // to get px,py to be the negative of px,py=>x2,y2
            // the dot product of two negated vectors is the same
            // as the dot product of the two normal vectors
            px = x2 - px;
            py = y2 - py;
            dotprod = px * x2 + py * y2;
            if (dotprod <= 0.0) {
                // px,py is on the side of x2,y2 away from x1,y1
                // distance to segment is length of (backwards) px,py vector
                // "length of its (clipped) projection" is now 0.0
                projlenSq = 0.0;
            } else {
                // px,py is between x1,y1 and x2,y2
                // dotprod is the length of the px,py vector
                // projected on the x2,y2=>x1,y1 vector times the
                // length of the x2,y2=>x1,y1 vector
                projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
            }
        }
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        // (which is zero if the projection falls outside the range
        // of the line segment).
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) {
            lenSq = 0;
        }
        return lenSq;
    }

    /**
     * Tests if the specified line segment intersects this line segment.
     * 
     * @param l
     *            the specified <code>Line2D</code>
     * @return <code>true</code> if this line segment and the specified line segment
     *         intersect each other; <code>false</code> otherwise.
     * @since 1.2
     */
    public boolean intersectsLine(Line2D l) {
        return linesIntersect(l.getX1(), l.getY1(), l.getX2(), l.getY2(), getX1(), getY1(), getX2(),
                getY2());
    }

    /**
     * Tests if the line segment from {@code (x1,y1)} to {@code (x2,y2)} intersects
     * the line segment from {@code (x3,y3)} to {@code (x4,y4)}.
     *
     * @param x1
     *            the X coordinate of the start point of the first specified line
     *            segment
     * @param y1
     *            the Y coordinate of the start point of the first specified line
     *            segment
     * @param x2
     *            the X coordinate of the end point of the first specified line
     *            segment
     * @param y2
     *            the Y coordinate of the end point of the first specified line
     *            segment
     * @param x3
     *            the X coordinate of the start point of the second specified line
     *            segment
     * @param y3
     *            the Y coordinate of the start point of the second specified line
     *            segment
     * @param x4
     *            the X coordinate of the end point of the second specified line
     *            segment
     * @param y4
     *            the Y coordinate of the end point of the second specified line
     *            segment
     * @return <code>true</code> if the first specified line segment and the second
     *         specified line segment intersect each other; <code>false</code>
     *         otherwise.
     * @since 1.2
     */
    private static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3,
            double y3, double x4, double y4) {
        return ((relativeCCW(x1, y1, x2, y2, x3, y3) * relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                && (relativeCCW(x3, y3, x4, y4, x1, y1)
                        * relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    /**
     * Returns an indicator of where the specified point {@code (px,py)} lies with
     * respect to the line segment from {@code (x1,y1)} to {@code (x2,y2)}. The
     * return value can be either 1, -1, or 0 and indicates in which direction the
     * specified line must pivot around its first end point, {@code (x1,y1)}, in
     * order to point at the specified point {@code (px,py)}.
     * <p>
     * A return value of 1 indicates that the line segment must turn in the
     * direction that takes the positive X axis towards the negative Y axis. In the
     * default coordinate system used by Java 2D, this direction is
     * counterclockwise.
     * <p>
     * A return value of -1 indicates that the line segment must turn in the
     * direction that takes the positive X axis towards the positive Y axis. In the
     * default coordinate system, this direction is clockwise.
     * <p>
     * A return value of 0 indicates that the point lies exactly on the line
     * segment. Note that an indicator value of 0 is rare and not useful for
     * determining collinearity because of floating point rounding issues.
     * <p>
     * If the point is colinear with the line segment, but not between the end
     * points, then the value will be -1 if the point lies "beyond {@code (x1,y1)}"
     * or 1 if the point lies "beyond {@code (x2,y2)}".
     *
     * @param x1
     *            the X coordinate of the start point of the specified line segment
     * @param y1
     *            the Y coordinate of the start point of the specified line segment
     * @param x2
     *            the X coordinate of the end point of the specified line segment
     * @param y2
     *            the Y coordinate of the end point of the specified line segment
     * @param px
     *            the X coordinate of the specified point to be compared with the
     *            specified line segment
     * @param py
     *            the Y coordinate of the specified point to be compared with the
     *            specified line segment
     * @return an integer that indicates the position of the third specified
     *         coordinates with respect to the line segment formed by the first two
     *         specified coordinates.
     * @since 1.2
     */
    private static int relativeCCW(double x1, double y1, double x2, double y2, double px,
            double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on. We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,y2
                // x2 and y2 are simply negated.
                // px and py need to have (x2 - x1) or (y2 - y1) subtracted
                // from them (based on the original values)
                // Since we really want to get a positive answer when the
                // point is "beyond (x2,y2)", then we want to calculate
                // the inverse anyway - thus we leave x2 & y2 negated.
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }
}
