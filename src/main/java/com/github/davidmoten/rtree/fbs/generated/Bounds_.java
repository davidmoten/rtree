// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class Bounds_ extends Table {
  public static Bounds_ getRootAsBounds_(ByteBuffer _bb) { return getRootAsBounds_(_bb, new Bounds_()); }
  public static Bounds_ getRootAsBounds_(ByteBuffer _bb, Bounds_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Bounds_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public BoxFloat_ boxFloat() { return boxFloat(new BoxFloat_()); }
  public BoxFloat_ boxFloat(BoxFloat_ obj) { int o = __offset(6); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public BoxDouble_ boxDouble() { return boxDouble(new BoxDouble_()); }
  public BoxDouble_ boxDouble(BoxDouble_ obj) { int o = __offset(8); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }

  public static void startBounds_(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addBoxFloat(FlatBufferBuilder builder, int boxFloatOffset) { builder.addStruct(1, boxFloatOffset, 0); }
  public static void addBoxDouble(FlatBufferBuilder builder, int boxDoubleOffset) { builder.addStruct(2, boxDoubleOffset, 0); }
  public static int endBounds_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

