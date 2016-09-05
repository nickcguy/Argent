package net.ncguy.argent.vpl.nodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 19/08/2016.
 */
public class BasicNodeFunctions {

    @NodeData(value = "Print String", argNames = "Text")
    public static void printString(VPLNode node, int pinId, String text) {
        System.out.println(text);
    }

    @NodeData(value = "Toast message", argNames = "Text")
    public static void toast(VPLNode node, int pinId, String text) {
        node.getGraph().info(text);
    }

    @NodeData(value = "Exec node", execIn = false, execOut = true, outPins = 0)
    public static void origin(VPLNode node, int pinId) {}

    @NodeData(value = "To String", execIn = false, execOut = false, argNames = "Object")
    public static String toString(VPLNode node, int pinId, Object obj) {
        if(obj == null) return "null";
        return obj.toString();
    }

    @NodeData(value = "Screenspace Cursor Position", execIn = false, execOut = false, argNames = "Position")
    public static Vector2 getScreenspaceCursorPos(VPLNode node, int pinId) {
        Vector2 pos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        pos.x /= Gdx.graphics.getWidth();
        pos.y /= Gdx.graphics.getHeight();
        return pos;
    }

}
