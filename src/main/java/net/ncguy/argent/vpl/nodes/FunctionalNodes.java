package net.ncguy.argent.vpl.nodes;

import net.ncguy.argent.utils.RandomString;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 22/08/2016.
 */
public class FunctionalNodes {

    @NodeData(value = "Random String", execIn = false, execOut = false, outPins = 1)
    public static String randomString(VPLNode node, int pinId) {
        return new RandomString(16).nextString();
    }

}
