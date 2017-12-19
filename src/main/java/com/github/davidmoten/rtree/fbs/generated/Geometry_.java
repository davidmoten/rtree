// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Geometry_ extends Table {
  public static Geometry_ getRootAsGeometry_(ByteBuffer _bb) { return getRootAsGeometry_(_bb, new Geometry_()); }
  public static Geometry_ getRootAsGeometry_(ByteBuffer _bb, Geometry_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Geometry_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Box_ box() { return box(new Box_()); }
  public Box_ box(Box_ obj) { int o = __offset(6); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public Point_ point() { return point(new Point_()); }
  public Point_ point(Point_ obj) { int o = __offset(8); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public Circle_ circle() { return circle(new Circle_()); }
  public Circle_ circle(Circle_ obj) { int o = __offset(10); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public Box_ line() { return line(new Box_()); }
  public Box_ line(Box_ obj) { int o = __offset(12); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }

  public static void startGeometry_(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addBox(FlatBufferBuilder builder, int boxOffset) { builder.addStruct(1, boxOffset, 0); }
  public static void addPoint(FlatBufferBuilder builder, int pointOffset) { builder.addStruct(2, pointOffset, 0); }
  public static void addCircle(FlatBufferBuilder builder, int circleOffset) { builder.addStruct(3, circleOffset, 0); }
  public static void addLine(FlatBufferBuilder builder, int lineOffset) { builder.addStruct(4, lineOffset, 0); }
  public static int endGeometry_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

