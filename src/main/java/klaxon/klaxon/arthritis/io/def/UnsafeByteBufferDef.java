package klaxon.klaxon.arthritis.io.def;

import klaxon.klaxon.hyphen.SerializerHandler;
import klaxon.klaxon.hyphen.codegen.MethodHandler;
import klaxon.klaxon.hyphen.codegen.def.BufferDef;
import klaxon.klaxon.hyphen.scan.type.Clazz;
import me.eigenraven.lwjgl3ify.api.Lwjgl3Aware;
import org.lwjgl.MemoryUtil;
import org.objectweb.asm.Opcodes;

import java.nio.ByteBuffer;

@Lwjgl3Aware
public class UnsafeByteBufferDef extends BufferDef {
    private final boolean unsafe;

    public UnsafeByteBufferDef(Clazz clazz, SerializerHandler<?, ?> serializerHandler) {
        super(clazz, serializerHandler);
        this.unsafe = clazz.containsAnnotation(DataUnsafeByteBuffer.class);
    }

    @Override
    protected void allocateBuffer(MethodHandler mh) {
        if (unsafe) {
            if (buffer != ByteBuffer.class) {
                throw new UnsupportedOperationException();
            }
            mh.callInst(Opcodes.INVOKESTATIC, MemoryUtil.class, "memAlloc", ByteBuffer.class, int.class);
        } else {
            super.allocateBuffer(mh);
        }
    }
}
