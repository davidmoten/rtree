package com.github.davidmoten.rtree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

	float[] low();
	
	float[] high();
	
    float low(int dimension);

    float high(int dimension);

    float area();

    Rectangle add(Rectangle r);

    boolean contains(float[] values);

    float intersectionArea(Rectangle r);

    float perimeter();

}