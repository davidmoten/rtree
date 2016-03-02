// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Node_ extends Table {
  public static Node_ getRootAsNode_(ByteBuffer _bb) { return getRootAsNode_(_bb, new Node_()); }
  public static Node_ getRootAsNode_(ByteBuffer _bb, Node_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Node_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Box_ mbb() { return mbb(new Box_()); }
  public Box_ mbb(Box_ obj) { int o = __offset(4); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public Node_ children(int j) { return children(new Node_(), j); }
  public Node_ children(Node_ obj, int j) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int childrenLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }
  public Entry_ entries(int j) { return entries(new Entry_(), j); }
  public Entry_ entries(Entry_ obj, int j) { int o = __offset(8); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int entriesLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }

  public static void startNode_(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addMbb(FlatBufferBuilder builder, int mbbOffset) { builder.addStruct(0, mbbOffset, 0); }
  public static void addChildren(FlatBufferBuilder builder, int childrenOffset) { builder.addOffset(1, childrenOffset, 0); }
  public static int createChildrenVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startChildrenVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addEntries(FlatBufferBuilder builder, int entriesOffset) { builder.addOffset(2, entriesOffset, 0); }
  public static int createEntriesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startEntriesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endNode_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

