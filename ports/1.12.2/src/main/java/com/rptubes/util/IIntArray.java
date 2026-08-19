package com.rptubes.util;

/** Compat shim for the newer IIntArray/ContainerData API on 1.12. */
public interface IIntArray {
    int get(int index);

    void set(int index, int value);

    int getCount();
}
