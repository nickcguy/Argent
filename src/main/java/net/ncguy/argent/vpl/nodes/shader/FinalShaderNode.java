package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 22/08/2016.
 */
public class FinalShaderNode {

    @NodeData(value = "Shader attributes", execIn = false, execOut = false, outPins = 0, tags = "shader", argNames = {"Data"})
    public static void shader(VPLNode node, int pinId, TextureData data) {

    }

    public static class TextureData {
        public int glID;
        public Vector2 texOffset, texScale;

        public TextureData(int glID, Vector2 texOffset, Vector2 texScale) {
            this.glID = glID;
            this.texOffset = texOffset;
            this.texScale = texScale;
        }
    }

}
