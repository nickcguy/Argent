package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.NodeData;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.nodes.shader.FinalShaderNode;

/**
 * Created by Guy on 22/08/2016.
 */
public class ShaderTextureDataDescriptor extends IStructDescriptor {

    @Override
    public Color colour() {
        return Color.CYAN;
    }

    @NodeData(value = "Make Texture Data", execIn = false, execOut = false, tags = "shader", argNames = {"Texture ID", "Texture UV Offset", "Texture UV Scale"})
    public static FinalShaderNode.TextureData makeTextureData(VPLNode node, int pinId, int glID, Vector2 texOffset, Vector2 texScale) {
        return new FinalShaderNode.TextureData(glID, texOffset, texScale);
    }

    @NodeData(value = "Break Texture Data", execIn = false, execOut = false, tags = "shader", outputTypes = {int.class, Vector2.class, Vector2.class}, outPins = 3, argNames = "Texture Data")
    public static IdentifierObject<?> breakTextureData(VPLNode node, int pinId, FinalShaderNode.TextureData data) {
        switch(MathUtils.clamp(pinId, 0, 2)) {
            case 0: return new IdentifierObject<>(int.class, data.glID);
            case 1: return new IdentifierObject<>(Vector2.class, data.texOffset);
            case 2: return new IdentifierObject<>(Vector2.class, data.texScale);
        }
        return new IdentifierObject<>(Integer.TYPE, data.glID);
    }

}
