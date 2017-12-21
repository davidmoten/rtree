// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

@SuppressWarnings("unused")
public final class LineDouble_ extends Struct {
  public LineDouble_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public double minX() { return bb.getDouble(bb_pos + 0); }
  public double minY() { return bb.getDouble(bb_pos + 8); }
  public double maxX() { return bb.getDouble(bb_pos + 16); }
  public double maxY() { return bb.getDouble(bb_pos + 24); }

  public static int createLineDouble_(FlatBufferBuilder builder, double minX, double minY, double maxX, double maxY) {
    builder.prep(8, 32);
    builder.putDouble(maxY);
    builder.putDouble(maxX);
    builder.putDouble(minY);
    builder.putDouble(minX);
    return builder.offset();
  }
};

