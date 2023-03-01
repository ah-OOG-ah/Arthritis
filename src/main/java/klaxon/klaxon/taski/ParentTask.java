package klaxon.klaxon.taski;

import javax.annotation.Nullable;

public interface ParentTask {
	 @Nullable
     Task getChild();
}
