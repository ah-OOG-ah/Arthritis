package klaxon.klaxon.taski.builtin;

import klaxon.klaxon.taski.Task;

import javax.annotation.Nullable;

public abstract class AbstractTask implements Task {
	private final String name;

	protected AbstractTask(String name) {
		this.name = name;
	}

	@Nullable
	public abstract String getProgressText();

	@Override
	public String getName() {
		return name;
	}

}
