// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class Geometry_ extends Table {
  public static Geometry_ getRootAsGeometry_(ByteBuffer _bb) { return getRootAsGeometry_(_bb, new Geometry_()); }
  public static Geometry_ getRootAsGeometry_(ByteBuffer _bb, Geometry_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Geometry_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public BoxFloat_ boxFloat() { return boxFloat(new BoxFloat_()); }
  public BoxFloat_ boxFloat(BoxFloat_ obj) { int o = __offset(6); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public PointFloat_ pointFloat() { return pointFloat(new PointFloat_()); }
  public PointFloat_ pointFloat(PointFloat_ obj) { int o = __offset(8); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public CircleFloat_ circleFloat() { return circleFloat(new CircleFloat_()); }
  public CircleFloat_ circleFloat(CircleFloat_ obj) { int o = __offset(10); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public BoxFloat_ lineFloat() { return lineFloat(new BoxFloat_()); }
  public BoxFloat_ lineFloat(BoxFloat_ obj) { int o = __offset(12); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public BoxDouble_ boxDouble() { return boxDouble(new BoxDouble_()); }
  public BoxDouble_ boxDouble(BoxDouble_ obj) { int o = __offset(14); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public PointDouble_ pointDouble() { return pointDouble(new PointDouble_()); }
  public PointDouble_ pointDouble(PointDouble_ obj) { int o = __offset(16); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public CircleDouble_ circleDouble() { return circleDouble(new CircleDouble_()); }
  public CircleDouble_ circleDouble(CircleDouble_ obj) { int o = __offset(18); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public BoxDouble_ lineDouble() { return lineDouble(new BoxDouble_()); }
  public BoxDouble_ lineDouble(BoxDouble_ obj) { int o = __offset(20); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }

  public static void startGeometry_(FlatBufferBuilder builder) { builder.startObject(9); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addBoxFloat(FlatBufferBuilder builder, int boxFloatOffset) { builder.addStruct(1, boxFloatOffset, 0); }
  public static void addPointFloat(FlatBufferBuilder builder, int pointFloatOffset) { builder.addStruct(2, pointFloatOffset, 0); }
  public static void addCircleFloat(FlatBufferBuilder builder, int circleFloatOffset) { builder.addStruct(3, circleFloatOffset, 0); }
  public static void addLineFloat(FlatBufferBuilder builder, int lineFloatOffset) { builder.addStruct(4, lineFloatOffset, 0); }
  public static void addBoxDouble(FlatBufferBuilder builder, int boxDoubleOffset) { builder.addStruct(5, boxDoubleOffset, 0); }
  public static void addPointDouble(FlatBufferBuilder builder, int pointDoubleOffset) { builder.addStruct(6, pointDoubleOffset, 0); }
  public static void addCircleDouble(FlatBufferBuilder builder, int circleDoubleOffset) { builder.addStruct(7, circleDoubleOffset, 0); }
  public static void addLineDouble(FlatBufferBuilder builder, int lineDoubleOffset) { builder.addStruct(8, lineDoubleOffset, 0); }
  public static int endGeometry_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

