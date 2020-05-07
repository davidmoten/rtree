// automatically generated by the FlatBuffers compiler, do not modify

package com.github.davidmoten.rtree.fbs.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Context_ extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static Context_ getRootAsContext_(ByteBuffer _bb) { return getRootAsContext_(_bb, new Context_()); }
  public static Context_ getRootAsContext_(ByteBuffer _bb, Context_ obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Context_ __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public com.github.davidmoten.rtree.fbs.generated.Bounds_ bounds() { return bounds(new com.github.davidmoten.rtree.fbs.generated.Bounds_()); }
  public com.github.davidmoten.rtree.fbs.generated.Bounds_ bounds(com.github.davidmoten.rtree.fbs.generated.Bounds_ obj) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public int minChildren() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int maxChildren() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createContext_(FlatBufferBuilder builder,
      int boundsOffset,
      int minChildren,
      int maxChildren) {
    builder.startTable(3);
    Context_.addMaxChildren(builder, maxChildren);
    Context_.addMinChildren(builder, minChildren);
    Context_.addBounds(builder, boundsOffset);
    return Context_.endContext_(builder);
  }

  public static void startContext_(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addBounds(FlatBufferBuilder builder, int boundsOffset) { builder.addOffset(0, boundsOffset, 0); }
  public static void addMinChildren(FlatBufferBuilder builder, int minChildren) { builder.addInt(1, minChildren, 0); }
  public static void addMaxChildren(FlatBufferBuilder builder, int maxChildren) { builder.addInt(2, maxChildren, 0); }
  public static int endContext_(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Context_ get(int j) { return get(new Context_(), j); }
    public Context_ get(Context_ obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

