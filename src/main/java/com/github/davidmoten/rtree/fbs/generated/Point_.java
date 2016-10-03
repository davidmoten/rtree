// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Point_ extends Struct {
	public Point_ __init(int _i, ByteBuffer _bb) {
		bb_pos = _i;
		bb = _bb;
		return this;
	}

	public float[] values() {
		int dimensions = bb.getInt(bb_pos);
		float[] result = new float[dimensions];
		for (int i = 0; i < dimensions; i++) {
			result[i] = bb.getFloat(bb_pos + 4 + i * 4);
		}
		return result;
	}

	public float values(int dimension) {
		return bb.getFloat(bb_pos + 4 + dimension * 4);
	}

	public static int createPoint_(FlatBufferBuilder builder, float[] values) {
		builder.prep(4, values.length * 4);
		for (int i = values.length - 1; i >= 0; i--) {
			builder.putFloat(values[i]);
		}
		builder.putInt(values.length);
		return builder.offset();
	}
};
