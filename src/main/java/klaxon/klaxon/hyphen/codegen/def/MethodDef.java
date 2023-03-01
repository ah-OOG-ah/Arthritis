package klaxon.klaxon.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public abstract class MethodDef implements SerializerDef {
	public final Map<Options, Boolean> options;
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;
	public final Clazz clazz;

	public MethodDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		this(handler, clazz, "");
	}

	public MethodDef(SerializerHandler<?, ?> handler, Clazz clazz, String suffix) {
		var ch = handler.codegenHandler;
		var definedClass = clazz.getDefinedClass();
		this.clazz = clazz;
		this.options = ch.options;
		this.getInfo = ch.createMethodInfo(clazz, "get", suffix, definedClass, ch.ioClass);
		this.putInfo = ch.createMethodInfo(clazz, "put", suffix, Void.TYPE, ch.ioClass, definedClass);
		this.measureInfo = ch.createMethodInfo(clazz, "measure", suffix, long.class, definedClass);
	}

	public abstract void scan(SerializerHandler<?, ?> handler, Clazz clazz);

	protected abstract void writeMethodPut(MethodHandler mh, Runnable valueLoad);

	protected abstract void writeMethodGet(MethodHandler mh);

	protected abstract void writeMethodMeasure(MethodHandler mh, Runnable valueLoad);

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.callInst(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		mh.callInst(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		mh.callInst(measureInfo);
	}

	public void writeMethods(CodegenHandler<?, ?> handler, CodegenHandler.MethodWriter writer, boolean spark) {
		if (!handler.options.get(Options.DISABLE_GET)) {
			writer.writeMethod(this.clazz, this.getInfo, spark, false, this::writeMethodGet);
		} else {
			writer.writeMethod(this.clazz, this.getInfo, spark, false, mh -> mh.throwException("get() is disabled."));
		}
		if (!handler.options.get(Options.DISABLE_PUT)) {
			writer.writeMethod(this.clazz, this.putInfo, spark, false, mh -> this.writeMethodPut(mh, () -> mh.parameterOp(ILOAD, 1)));
		} else {
			writer.writeMethod(this.clazz, this.putInfo, spark, false, mh -> mh.throwException("put() is disabled."));
		}
		if (!handler.options.get(Options.DISABLE_MEASURE) && this.hasDynamicSize()) {
			writer.writeMethod(this.clazz, this.measureInfo, spark, false, mh -> {
				this.writeMethodMeasure(mh, () -> mh.parameterOp(ILOAD, 0));
				if (spark) {
					mh.visitLdcInsn(getStaticSize());
					mh.op(LADD);
				}
			});
		} else {
			writer.writeMethod(this.clazz, this.measureInfo, spark, false, mh -> mh.throwException("measure() is disabled."));
		}


	}
}
