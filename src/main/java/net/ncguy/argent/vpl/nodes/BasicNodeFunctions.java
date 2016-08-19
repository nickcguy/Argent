package net.ncguy.argent.vpl.nodes;

import net.ncguy.argent.vpl.NodeData;

/**
 * Created by Guy on 19/08/2016.
 */
public class BasicNodeFunctions {

    @NodeData("Print String")
    public static void printString(String text) {
        System.out.println(text);
    }

}
