package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

final class RectangleImpl implements Rectangle {
    private final float x1, y1, x2, y2;

    private RectangleImpl(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    static RectangleImpl create(double x1, double y1, double x2, double y2) {
        return new RectangleImpl((float) x1, (float) y1, (float) x2, (float) y2);
    }

    static RectangleImpl create(float x1, float y1, float x2, float y2) {
        return new RectangleImpl(x1, y1, x2, y2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x1()
     */
    @Override
    public float x1() {
        return x1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y1()
     */
    @Override
    public float y1() {
        return y1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x2()
     */
    @Override
    public float x2() {
        return x2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y2()
     */
    @Override
    public float y2() {
        return y2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#area()
     */
    @Override
    public float area() {
        return (x2 - x1) * (y2 - y1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.davidmoten.rtree.geometry.RectangleI#add(com.github.davidmoten
     * .rtree.geometry.Rectangle)
     */
    @Override
    public RectangleImpl add(Rectangle r) {
        return new RectangleImpl(min(x1, r.x1()), min(y1, r.y1()), max(x2, r.x2()), max(y2, r.y2()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#contains(double,
     * double)
     */
    @Override
    public boolean contains(double x, double y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return r.x2() >= x1 && r.x1() <= x2 && r.y2() >= y1 && r.y1() <= y2;
    }

    @Override
    public double distance(Rectangle r) {
        if (intersects(r))
            return 0;
        else {
            Rectangle mostLeft = x1 < r.x1() ? this : r;
            Rectangle mostRight = x1 > r.x1() ? this : r;
            double xDifference = max(0,
                    mostLeft.x1() == mostRight.x1() ? 0 : mostRight.x1() - mostLeft.x2());

            Rectangle upper = y1 < r.y1() ? this : r;
            Rectangle lower = y1 > r.y1() ? this : r;

            double yDifference = max(0, upper.y1() == lower.y1() ? 0 : lower.y1() - upper.y2());

            return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        }
    }

    @Override
    public RectangleImpl mbr() {
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
        Optional<RectangleImpl> other = ObjectsHelper.asClass(obj, RectangleImpl.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
        } else
            return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.davidmoten.rtree.geometry.RectangleI#intersectionArea(com.
     * github.davidmoten.rtree.geometry.Rectangle)
     */
    @Override
    public float intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else
            return create(max(x1, r.x1()), max(y1, r.y1()), min(x2, r.x2()), min(y2, r.y2()))
                    .area();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#perimeter()
     */
    @Override
    public float perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    private static float max(float a, float b) {
        if (a < b)
            return b;
        else
            return a;
    }

    private static float min(float a, float b) {
        if (a < b)
            return a;
        else
            return b;
    }

}