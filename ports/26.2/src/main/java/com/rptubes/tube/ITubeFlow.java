package com.rptubes.tube;

/**
 * Port of eloraam.core.ITubeFlow — blocks that hold in-flight tube items.
 */
public interface ITubeFlow extends ITubeConnectable {
    void addTubeItem(TubeItem item);

    TubeFlow getTubeFlow();
}
