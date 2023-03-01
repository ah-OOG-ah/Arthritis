package klaxon.klaxon.arthritis.io;

import klaxon.klaxon.hyphen.HyphenSerializer;
import klaxon.klaxon.hyphen.SerializerFactory;
import klaxon.klaxon.hyphen.io.ByteBufferIO;
import klaxon.klaxon.arthritis.registry.data.ChunkData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class Serializer<O> {
    private final HyphenSerializer<ByteBufferIO, O> serializer;

    public Serializer(Class<O> aClass) {
        var factory = SerializerFactory.createDebug(ByteBufferIO.class, aClass);
        factory.addGlobalAnnotation(ChunkData.class, dev.quantumfusion.hyphen.scan.annotations.DataSubclasses.class, new Class[]{ChunkData.class});
        factory.setClassName(getSerializerClassName(aClass));
        factory.addDynamicDef(ByteBuffer.class, UnsafeByteBufferDef::new);
        this.serializer = factory.build();
    }

    public O get(dev.quantumfusion.hyphen.io.ByteBufferIO io) {
        return this.serializer.get(io);
    }

    public void put(dev.quantumfusion.hyphen.io.ByteBufferIO io, O data) {
        this.serializer.put(io, data);
    }

    public long measure(O data) {
        return this.serializer.measure(data);
    }

    public void save(Path path, StepTask task, O data) {
        var measure = (int) this.serializer.measure(data);
        var io = dev.quantumfusion.hyphen.io.ByteBufferIO.createDirect(measure);
        this.serializer.put(io, data);
        io.rewind();
        try {

            IOHelper.save(path, task, io, measure, ConfigHandler.INSTANCE.config.compression);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public O load(Path path) {
        try {
            dev.quantumfusion.hyphen.io.ByteBufferIO io = IOHelper.load(path);
            return this.serializer.get(io);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static <O> String getSerializerClassName(Class<O> holderClass) {
        return holderClass.getSimpleName().toLowerCase() + "-serializer";
    }
}
