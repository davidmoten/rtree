package com.github.davidmoten.rtree.geometry;

import java.util.Arrays;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

final class RectangleImpl implements Rectangle {
    private final float[] low;
    private final float[] high;

    private RectangleImpl(float[] low, float[] high) {
    	for (int i = 0; i < low.length; i++) {
    		Preconditions.checkArgument(high[i] >= low[i]);
    	}
        this.low = low;
        this.high = high;
    }

    /*static Rectangle create(double x1, double y1, double x2, double y2) {
        return new RectangleImpl((float) x1, (float) y1, (float) x2, (float) y2);
    }*/

    static Rectangle create(float[] low, float[] high) {
        return new RectangleImpl(low, high);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x1()
     */
    @Override
    public float low(int dimension) {
        return low[dimension];
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y1()
     */
    @Override
    public float high(int dimension) {
        return high[dimension];
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x2()
     */
    @Override
    public float[] low() {
        return low;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y2()
     */
    @Override
    public float[] high() {
        return high;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#area()
     */
    @Override
    public float area() {
    	float result = 1f;
    	for (int i = 0; i < low.length; i++) {
    		result *= high[i] - low[i];
    	}
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.davidmoten.rtree.geometry.RectangleI#add(com.github.davidmoten
     * .rtree.geometry.Rectangle)
     */
    @Override
    public Rectangle add(Rectangle r) {
    	float[] low = new float[this.low.length];
    	float[] high = new float[this.high.length];
    	
    	for (int i = 0; i < low.length; i++) {
    		low[i] = min(r.low(i), this.low(i));
    	}
    	
    	for (int i = 0; i < high.length; i++) {
    		high[i] = max(r.high(i), this.high(i));
    	}
    	
        return new RectangleImpl(low, high);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#contains(double,
     * double)
     */
    @Override
    public boolean contains(float[] values) {
    	for (int i = 0; i < low.length; i++) {
    		if ((high[i] < values[i]) || (low[i] > values[i])) {
    			return false;
    		}
    	}
        return true;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return intersects(low, high, r.low(), r.high());
        // return r.x2() >= x1 && r.x1() <= x2 && r.y2() >= y1 && r.y1() <= y2;
    }

    @Override
    public double distance(Rectangle r) {
        return distance(low, high, r.low(), r.high());
        // if (intersects(r))
        // return 0;
        // else {
        // Rectangle mostLeft = x1 < r.x1() ? this : r;
        // Rectangle mostRight = x1 > r.x1() ? this : r;
        // double xDifference = max(0,
        // mostLeft.x1() == mostRight.x1() ? 0 : mostRight.x1() -
        // mostLeft.x2());
        //
        // Rectangle upper = y1 < r.y1() ? this : r;
        // Rectangle lower = y1 > r.y1() ? this : r;
        //
        // double yDifference = max(0, upper.y1() == lower.y1() ? 0 : lower.y1()
        // - upper.y2());
        //
        // return Math.sqrt(xDifference * xDifference + yDifference *
        // yDifference);
        // }
    }

    public static double distance(float[] low1, float[] high1, float[] low2, float[] high2) {
        if (intersects(low1, high1, low2, high2)) {
            return 0;
        }
        float sum = 0f;
        
        for (int i = 0; i < low1.length; i++) {
        	boolean xyMostLeft = low1[i] < low2[i];
        	float mostLeftX1 = xyMostLeft ? low1[i] : low2[i];
        	float mostRightX1 = xyMostLeft ? low2[i] : low1[i];
        	float mostLeftX2 = xyMostLeft ? high1[i] : high2[i];
        	double xDifference = max(0, mostLeftX1 == mostRightX1 ? 0 : mostRightX1 - mostLeftX2);
        	sum += xDifference * xDifference;
        }

        return Math.sqrt(sum);
    }

    private static boolean intersects(float[] low1, float[] high1, float[] low2, float[] high2) {
    	for (int i = 0; i < low1.length; i++) {
    		if ((low1[i] > high2[i]) || (low2[i] > high1[i])) {
    			return false;
    		}
    	}
    	return true;
        //return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2;
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder("[");
    	for (int i = 0; i < low.length; i++) {
    		builder.append(low[i]);
    		builder.append("\t");
    	}
    	builder.append("-");
    	for (int i = 0; i < high.length; i++) {
    		builder.append(high[i]);
    		if (i < high.length - 1) {
    			builder.append("\t");
    		}
    	}
    	builder.append("]");
    	return builder.toString();
    	
        //return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
    }

    @Override
    public int hashCode() {
    	float[] tmp = new float[low.length * 2];
    	for (int i = 0; i < low.length; i++) {
    		tmp[i] = low[i];
    		tmp[i + low.length] = high[i];
    	}
    	return Arrays.hashCode(tmp);
        //return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<RectangleImpl> other = ObjectsHelper.asClass(obj, RectangleImpl.class);
        if (other.isPresent()) {
        	for (int i = 0; i < low.length; i++) {
        		if (!(Objects.equal(low(i), other.get().low(i)) && Objects.equal(high(i), other.get().high(i))))
        			return false;
        	}
            return true;
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
        else {
        	float[] low = new float[this.low.length];
	    	float[] high = new float[this.high.length];
	    	
	    	for (int i = 0; i < low.length; i++) {
	    		low[i] = max(r.low(i), this.low(i));
	    	}
	    	
	    	for (int i = 0; i < high.length; i++) {
	    		high[i] = min(r.high(i), this.high(i));
	    	}
        	
            return create(low, high).area();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#perimeter()
     */
    @Override
    public float perimeter() {
    	float result = 0;
    	for (int i = 0; i < low.length; i++) {
    		result += 2 * (high[i] - low[i]);
    	}
        return result;
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