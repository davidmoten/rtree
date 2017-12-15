package com.github.davidmoten.rtree.geometry;

/**
 * A geometrical region that represents an Entry spatially. It is recommended
 * that implementations of this interface implement equals() and hashCode()
 * appropriately that entry equality checks work as expected.
 */
public interface Geometry {

    /**
     * <p>
     * Returns the distance to the given {@link Rectangle}. For a
     * {@link Rectangle} this might be Euclidean distance but for an EPSG4326
     * lat-long Rectangle might be great-circle distance. The distance function
     * should satisfy the following properties:
     * </p>
     * 
     * <p>
     * <code>distance(r) &gt;= 0</code>
     * </p>
     * 
     * <p>
     * <code>if r1 contains r2 then distance(r1)&lt;=distance(r2)</code>
     * </p>
     * 
     * 
     * @param r
     *            rectangle to measure distance to
     * @return distance to the rectangle r from the geometry
     */
    double distance(Rectangle r);

    /**
     * Returns the minimum bounding rectangle of this geometry.
     * 
     * @return minimum bounding rectangle
     */
    Rectangle mbr();

    boolean intersects(Rectangle r);
}
