package klaxon.klaxon.arthritis.api;

import java.util.function.BiFunction;

public class MissingHandler<R> {
    public final Class<R> parentClass;
    public final BiFunction<R, RegistryWriter, DashObject<?>> func;

    public MissingHandler(Class<R> parentClass, BiFunction<R, RegistryWriter, DashObject<?>> func) {
        this.parentClass = parentClass;
        this.func = func;
    }
}
