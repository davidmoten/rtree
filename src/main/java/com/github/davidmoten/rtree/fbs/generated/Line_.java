// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Line_ extends Struct {
  public Line_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float minX() { return bb.getFloat(bb_pos + 0); }
  public float minY() { return bb.getFloat(bb_pos + 4); }
  public float maxX() { return bb.getFloat(bb_pos + 8); }
  public float maxY() { return bb.getFloat(bb_pos + 12); }

  public static int createLine_(FlatBufferBuilder builder, float minX, float minY, float maxX, float maxY) {
    builder.prep(4, 16);
    builder.putFloat(maxY);
    builder.putFloat(maxX);
    builder.putFloat(minY);
    builder.putFloat(minX);
    return builder.offset();
  }
};

