package com.github.davidmoten.rtree.geometry;

public final class Point implements Rectangle {

    private final float[] values;

    private Point(float[] values) {
        this.values = values;
    }

    /*static Point create(double x, double y) {
        return new Point((float) x, (float) y);
    }*/

    static Point create(float[] values) {
        return new Point(values);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return RectangleImpl.distance(values, values, r.low(), r.high());
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(Point p) {
    	float result = 0;
    	float dval;
    	for (int i = 0; i < p.values.length; i++) {
    		dval = this.values[i] - p.values[i];
    		result += dval * dval;
    	}
        return result;
    }

    @Override
    public boolean intersects(Rectangle r) {
    	for (int i = 0; i < values.length; i++) {
    		if ((r.low(i) > values[i]) || (values[i] > r.high(i)))
    			return false;
    	}
        return true;
    }

    public float[] values() {
        return values;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < values.length; i++) {
        	result = prime * result + Float.floatToIntBits(values[i]);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        for (int i = 0; i < values.length; i++) {
        	if (Float.floatToIntBits(values[i]) != Float.floatToIntBits(other.values[i]))
        		return false;
        }
        return true;
    }

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder("[");
    	for (int i = 0; i < values.length; i++) {
    		builder.append(values[i]);
    		if (i < values.length - 1) {
    			builder.append("\t");
    		}
    	}
    	builder.append("]");
    	return builder.toString();
        //return "Point [x=" + x() + ", y=" + y() + "]";
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public float low(int dimension) {
        return values[dimension];
    }

    @Override
    public float high(int dimension) {
    	return values[dimension];
    }
    
    @Override
    public float[] low() {
        return values;
    }

    @Override
    public float[] high() {
    	return values;
    }

    @Override
    public float area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
    	float[] low = new float[values.length];
    	float[] high = new float[values.length];
    	
    	for (int i = 0; i < values.length; i++) {
    		low[i] = Math.min(values[i], r.low(i));
    		high[i] = Math.max(values[i], r.high(i));
    	}
    	
        return RectangleImpl.create(low, high);
    }

    @Override
    public boolean contains(float[] values) {
    	for (int i = 0; i < values.length; i++) {
    		if (this.values[i] != values[i]) {
    			return false;
    		}
    	}
    	
        return true;
    }

    @Override
    public float intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public float perimeter() {
        return 0;
    }

}