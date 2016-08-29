package net.ncguy.argent.vpl;

/**
 * Created by Guy on 28/08/2016.
 */
public interface VPLPinListener {

    void connected(VPLPin other);
    void disconnected(VPLPin other);

}
