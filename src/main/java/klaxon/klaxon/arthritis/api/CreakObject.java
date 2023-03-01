package klaxon.klaxon.arthritis.api;

import klaxon.klaxon.arthritis.registry.RegistryReader;

/**
 * The Exportable interface is the interface to implement when adding DashLoader cache support to a registry object.
 *
 * @param <R> Raw Object.
 */
public interface CreakObject<R> {
    /**
     * Runs before {@link CreakObject#export(RegistryReader)} on the main thread.
     */
    @SuppressWarnings("unused")
    default void preExport(RegistryReader reader) {
    }

    /**
     * Runs in parallel returning the target object.
     */
    @SuppressWarnings("unused")
    R export(RegistryReader reader);

    /**
     * Runs after {@link CreakObject#export(RegistryReader)} on the main thread.
     */
    @SuppressWarnings("unused")
    default void postExport(RegistryReader reader) {
    }
}
