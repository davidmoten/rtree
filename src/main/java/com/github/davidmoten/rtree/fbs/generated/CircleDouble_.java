// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

@SuppressWarnings("unused")
public final class CircleDouble_ extends Struct {
  public CircleDouble_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public double x() { return bb.getDouble(bb_pos + 0); }
  public double y() { return bb.getDouble(bb_pos + 8); }
  public double radius() { return bb.getDouble(bb_pos + 16); }

  public static int createCircleDouble_(FlatBufferBuilder builder, double x, double y, double radius) {
    builder.prep(8, 24);
    builder.putDouble(radius);
    builder.putDouble(y);
    builder.putDouble(x);
    return builder.offset();
  }
};

