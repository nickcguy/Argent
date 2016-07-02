package net.ncguy.argent.editor.swing.shader.internal;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 30/06/2016.
 */
public interface IShaderDescriptor {

    String name();
    String desc();
    String frag();
    String vert();
    default DynamicShader.GLPrimitiveType type() { return DynamicShader.GLPrimitiveType.GL_TRIANGLES; }

    BufferRenderer<?> constructRenderer(GameWorld.Generic<?> gameWorld);

    default ShaderProgram compile() {
        return new ShaderProgram(vert(), frag());
    }

}
