package com.rptubes.tube;

/**
 * Port of eloraam.core.ITubeConnectable — the contract tubes and tube machines share.
 */
public interface ITubeConnectable {
    /** Bitmask of connectable sides (bit 0 = DOWN … bit 5 = EAST, Minecraft ForgeDirection ordinal). */
    int getTubeConnectableSides();

    /** Compatibility class. 0 = normal pneumatic tube. */
    int getTubeConClass();

    boolean canRouteItems();

    boolean tubeItemEnter(int side, int state, TubeItem item);

    boolean tubeItemCanEnter(int side, int state, TubeItem item);

    int tubeWeight(int side, int state);
}
