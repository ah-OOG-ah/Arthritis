package klaxon.klaxon.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.GOTO;

public class While implements AutoCloseable {
	protected final MethodHandler mh;
	protected final Label start = new Label();
	protected final Label stop = new Label();

	protected While(MethodHandler mh) {
		this.mh = mh;
		this.mh.visitLabel(start);
	}

	public static While create(MethodHandler mh) {
		return new While(mh);
	}

	public void exit(int op) {
		this.mh.visitJumpInsn(op, stop);
	}

	@Override
	public void close() {
		this.mh.visitJumpInsn(GOTO, start);
		this.mh.visitLabel(stop);
	}
}
