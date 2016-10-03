// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Box_ extends Struct {
	public Box_ __init(int _i, ByteBuffer _bb) {
		bb_pos = _i;
		bb = _bb;
		return this;
	}

	/*
	 * public float minX() { return bb.getFloat(bb_pos + 0); } public float
	 * minY() { return bb.getFloat(bb_pos + 4); } public float maxX() { return
	 * bb.getFloat(bb_pos + 8); } public float maxY() { return
	 * bb.getFloat(bb_pos + 12); }
	 */
	public float[] min() {
		int d = bb.getInt(bb_pos + 0);
		float[] result = new float[d];
		for (int i = 0; i < d; i++) {
			result[i] = bb.getFloat(bb_pos + 4 + i * 4);
		}
		return result;
	}

	public float[] max() {
		int d = bb.getInt(bb_pos + 0);
		float[] result = new float[d];
		for (int i = 0; i < d; i++) {
			result[i] = bb.getFloat(bb_pos + 4 + i * 4 + d * 4);
		}
		return result;
	}

	public float min(int dimension) {
		return bb.getFloat(bb_pos + 4 + dimension * 4);
	}

	public float max(int dimension) {
		int d = bb.getInt(bb_pos + 0);
		return bb.getFloat(bb_pos + 4 + dimension * 4 + d * 4);
	}

	public static int createBox_(FlatBufferBuilder builder, float[] min, float[] max) {
		builder.prep(4, min.length * 8);
		for (int i = max.length - 1; i >= 0; i--) {
			builder.putFloat(max[i]);
		}
		for (int i = min.length - 1; i >= 0; i--) {
			builder.putFloat(min[i]);
		}
		builder.putInt(min.length);
		/*
		 * builder.putFloat(maxY); builder.putFloat(maxX);
		 * builder.putFloat(minY); builder.putFloat(minX);
		 */
		return builder.offset();
	}
};
