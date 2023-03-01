package klaxon.klaxon.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.io.UnsafeIO;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.objectweb.asm.Opcodes.*;

public class StringIODef implements SerializerDef {
	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "putString", Void.TYPE, String.class);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "getString", String.class);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		if (mh.ioClass == UnsafeIO.class) {
			mh.callInst(INVOKESTATIC, UnsafeIO.class, "getStringBytes", int.class, String.class);
		} else {
			// kinda bad for speed, but it's kinda our only option here
			mh.visitFieldInsn(GETSTATIC, StandardCharsets.class, "UTF_8", Charset.class);
			mh.callInst(INVOKEVIRTUAL, String.class, "getBytes", byte[].class, Charset.class);
			mh.op(ARRAYLENGTH, I2L);
			mh.visitLdcInsn(4L);
			mh.op(LADD);
		}
	}
}
