// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

@SuppressWarnings("unused")
public final class CircleFloat_ extends Struct {
  public CircleFloat_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float x() { return bb.getFloat(bb_pos + 0); }
  public float y() { return bb.getFloat(bb_pos + 4); }
  public float radius() { return bb.getFloat(bb_pos + 8); }

  public static int createCircleFloat_(FlatBufferBuilder builder, float x, float y, float radius) {
    builder.prep(4, 12);
    builder.putFloat(radius);
    builder.putFloat(y);
    builder.putFloat(x);
    return builder.offset();
  }
};

