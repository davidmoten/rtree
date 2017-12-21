// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;

@SuppressWarnings("unused")
public final class LineFloat_ extends Struct {
  public LineFloat_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float minX() { return bb.getFloat(bb_pos + 0); }
  public float minY() { return bb.getFloat(bb_pos + 4); }
  public float maxX() { return bb.getFloat(bb_pos + 8); }
  public float maxY() { return bb.getFloat(bb_pos + 12); }

  public static int createLineFloat_(FlatBufferBuilder builder, float minX, float minY, float maxX, float maxY) {
    builder.prep(4, 16);
    builder.putFloat(maxY);
    builder.putFloat(maxX);
    builder.putFloat(minY);
    builder.putFloat(minX);
    return builder.offset();
  }
};

