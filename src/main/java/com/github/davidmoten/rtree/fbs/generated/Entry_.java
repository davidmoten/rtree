// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Entry_ extends Table {
  public static Entry_ getRootAsEntry_(ByteBuffer _bb) { return getRootAsEntry_(_bb, new Entry_()); }
  public static Entry_ getRootAsEntry_(ByteBuffer _bb, Entry_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Entry_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Geometry_ geometry() { return geometry(new Geometry_()); }
  public Geometry_ geometry(Geometry_ obj) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public byte object(int j) { int o = __offset(6); return o != 0 ? bb.get(__vector(o) + j * 1) : 0; }
  public int objectLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer objectAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }

  public static int createEntry_(FlatBufferBuilder builder,
      int geometryOffset,
      int objectOffset) {
    builder.startObject(2);
    Entry_.addObject(builder, objectOffset);
    Entry_.addGeometry(builder, geometryOffset);
    return Entry_.endEntry_(builder);
  }

  public static void startEntry_(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addGeometry(FlatBufferBuilder builder, int geometryOffset) { builder.addOffset(0, geometryOffset, 0); }
  public static void addObject(FlatBufferBuilder builder, int objectOffset) { builder.addOffset(1, objectOffset, 0); }
  public static int createObjectVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startObjectVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endEntry_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

