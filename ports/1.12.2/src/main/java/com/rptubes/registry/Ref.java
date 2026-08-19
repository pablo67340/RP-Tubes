package com.rptubes.registry;

/**
 * Minimal stand-in for RegistryObject so call sites keep the familiar
 * {@code ModBlocks.FILTER.get()} shape on 1.12, where objects are
 * constructed eagerly in static initializers.
 */
public final class Ref<T> {
    private final T value;

    public Ref(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }
}
