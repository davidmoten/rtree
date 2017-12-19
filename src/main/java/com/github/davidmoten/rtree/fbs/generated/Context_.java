// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Context_ extends Table {
  public static Context_ getRootAsContext_(ByteBuffer _bb) { return getRootAsContext_(_bb, new Context_()); }
  public static Context_ getRootAsContext_(ByteBuffer _bb, Context_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Context_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Box_ bounds() { return bounds(new Box_()); }
  public Box_ bounds(Box_ obj) { int o = __offset(4); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public int minChildren() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int maxChildren() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static void startContext_(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addBounds(FlatBufferBuilder builder, int boundsOffset) { builder.addStruct(0, boundsOffset, 0); }
  public static void addMinChildren(FlatBufferBuilder builder, int minChildren) { builder.addInt(1, minChildren, 0); }
  public static void addMaxChildren(FlatBufferBuilder builder, int maxChildren) { builder.addInt(2, maxChildren, 0); }
  public static int endContext_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

