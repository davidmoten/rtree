// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

@SuppressWarnings("unused")
public final class PointFloat_ extends Struct {
  public PointFloat_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float x() { return bb.getFloat(bb_pos + 0); }
  public float y() { return bb.getFloat(bb_pos + 4); }

  public static int createPointFloat_(FlatBufferBuilder builder, float x, float y) {
    builder.prep(4, 8);
    builder.putFloat(y);
    builder.putFloat(x);
    return builder.offset();
  }
};

