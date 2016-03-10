package com.github.davidmoten.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import com.github.davidmoten.rtree.fbs.SerializerFlatBuffers;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rx.Functions;

import rx.functions.Func1;

public final class Serializers {

    private Serializers() {
        // prevent instantiation
    }

    public static class SerializerFlatBuffersBuilder {

        private SerializerFlatBuffersBuilder() {

        }

        public <T> SerializerFlatBuffersTypedBuilder<T> serializer(
                Func1<? super T, byte[]> serializer) {
            return new SerializerFlatBuffersTypedBuilder<T>(serializer, null);
        }

        public <T> SerializerFlatBuffersTypedBuilder<T> deserializer(
                Func1<byte[], ? extends T> deserializer) {
            return new SerializerFlatBuffersTypedBuilder<T>(null, deserializer);
        }

        public <S extends Geometry> Serializer<String, S> string(Charset charset) {
            Func1<String, byte[]> serializer = createStringSerializer(charset);
            Func1<byte[], String> deserializer = createStringDeserializer(charset);
            return new SerializerFlatBuffersTypedBuilder<String>(serializer, deserializer).create();
        }

        @SuppressWarnings("unchecked")
        public <T extends Serializable, S extends Geometry> Serializer<T, S> javaIo() {
            Func1<T, byte[]> serializer = (Func1<T, byte[]>) javaIoSerializer();
            Func1<byte[], T> deserializer = (Func1<byte[], T>) javaIoDeserializer();
            return new SerializerFlatBuffersTypedBuilder<T>(serializer, deserializer).create();
        }

        public <S extends Geometry> Serializer<String, S> utf8() {
            return string(Charset.forName("UTF-8"));
        }

        public <S extends Geometry> Serializer<byte[], S> bytes() {
            Func1<byte[], byte[]> serializer = Functions.identity();
            Func1<byte[], byte[]> deserializer = Functions.identity();
            return new SerializerFlatBuffersTypedBuilder<byte[]>(serializer, deserializer).create();
        }

    }

    public static final class SerializerFlatBuffersTypedBuilder<T> {

        private Func1<? super T, byte[]> serializer;
        private Func1<byte[], ? extends T> deserializer;

        private SerializerFlatBuffersTypedBuilder(Func1<? super T, byte[]> serializer,
                Func1<byte[], ? extends T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        public SerializerFlatBuffersTypedBuilder<T> serializer(
                Func1<? super T, byte[]> serializer) {
            this.serializer = serializer;
            return this;
        }

        public SerializerFlatBuffersTypedBuilder<T> deserializer(
                Func1<byte[], ? extends T> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <S extends Geometry> Serializer<T, S> create() {
            if (serializer == null) {
                serializer = (Func1<T, byte[]>) javaIoSerializer();
            }
            if (deserializer == null) {
                deserializer = (Func1<byte[],T>) javaIoDeserializer();
            }
            return SerializerFlatBuffers.create(serializer, deserializer);
        }

    }

    public static <T, S extends Geometry> SerializerFlatBuffersBuilder flatBuffers() {
        return new SerializerFlatBuffersBuilder();
    }

    private static Func1<String, byte[]> createStringSerializer(final Charset charset) {
        return new Func1<String, byte[]>() {
            @Override
            public byte[] call(String s) {
                return s.toString().getBytes(charset);
            }
        };
    }

    private static <T> Func1<byte[], String> createStringDeserializer(final Charset charset) {
        return new Func1<byte[], String>() {
            @Override
            public String call(byte[] bytes) {
                return new String(bytes, charset);
            }
        };
    }

    private static Func1<Serializable, byte[]> javaIoSerializer() {
        return new Func1<Serializable, byte[]>() {
            @Override
            public byte[] call(Serializable o) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(bytes);
                    oos.writeObject(o);
                    oos.close();
                    return bytes.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (oos != null)
                            oos.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        };
    }

    private static Func1<byte[], Serializable> javaIoDeserializer() {
        return new Func1<byte[], Serializable>() {
            @Override
            public Serializable call(byte[] bytes) {
                ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(is);
                    return (Serializable) ois.readObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (ois != null)
                        try {
                            ois.close();
                        } catch (IOException e) {
                            // ignore
                        }
                }
            }
        };
    }

}
