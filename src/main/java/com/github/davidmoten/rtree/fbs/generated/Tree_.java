// automatically generated, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class Tree_ extends Table {
  public static Tree_ getRootAsTree_(ByteBuffer _bb) { return getRootAsTree_(_bb, new Tree_()); }
  public static Tree_ getRootAsTree_(ByteBuffer _bb, Tree_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Tree_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Context_ context() { return context(new Context_()); }
  public Context_ context(Context_ obj) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public Node_ root() { return root(new Node_()); }
  public Node_ root(Node_ obj) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public long size() { int o = __offset(8); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }

  public static int createTree_(FlatBufferBuilder builder,
      int contextOffset,
      int rootOffset,
      long size) {
    builder.startObject(3);
    Tree_.addSize(builder, size);
    Tree_.addRoot(builder, rootOffset);
    Tree_.addContext(builder, contextOffset);
    return Tree_.endTree_(builder);
  }

  public static void startTree_(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addContext(FlatBufferBuilder builder, int contextOffset) { builder.addOffset(0, contextOffset, 0); }
  public static void addRoot(FlatBufferBuilder builder, int rootOffset) { builder.addOffset(1, rootOffset, 0); }
  public static void addSize(FlatBufferBuilder builder, long size) { builder.addInt(2, (int)size, 0); }
  public static int endTree_(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishTree_Buffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

