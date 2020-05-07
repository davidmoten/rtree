// automatically generated by the FlatBuffers compiler, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Bounds_ extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static Bounds_ getRootAsBounds_(ByteBuffer _bb) { return getRootAsBounds_(_bb, new Bounds_()); }
  public static Bounds_ getRootAsBounds_(ByteBuffer _bb, Bounds_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Bounds_ __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public com.github.davidmoten.rtree.fbs.generated.BoxFloat_ boxFloat() { return boxFloat(new com.github.davidmoten.rtree.fbs.generated.BoxFloat_()); }
  public com.github.davidmoten.rtree.fbs.generated.BoxFloat_ boxFloat(com.github.davidmoten.rtree.fbs.generated.BoxFloat_ obj) { int o = __offset(6); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  public com.github.davidmoten.rtree.fbs.generated.BoxDouble_ boxDouble() { return boxDouble(new com.github.davidmoten.rtree.fbs.generated.BoxDouble_()); }
  public com.github.davidmoten.rtree.fbs.generated.BoxDouble_ boxDouble(com.github.davidmoten.rtree.fbs.generated.BoxDouble_ obj) { int o = __offset(8); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }

  public static void startBounds_(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addBoxFloat(FlatBufferBuilder builder, int boxFloatOffset) { builder.addStruct(1, boxFloatOffset, 0); }
  public static void addBoxDouble(FlatBufferBuilder builder, int boxDoubleOffset) { builder.addStruct(2, boxDoubleOffset, 0); }
  public static int endBounds_(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Bounds_ get(int j) { return get(new Bounds_(), j); }
    public Bounds_ get(Bounds_ obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

