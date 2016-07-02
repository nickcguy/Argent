package net.ncguy.argent.editor.swing.shader.internal.simpledepth;

import net.ncguy.argent.editor.swing.shader.internal.IShaderDescriptor;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.sample.DepthRenderer;
import net.ncguy.argent.utils.Reference;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 30/06/2016.
 */
public class SimpleDepthDescriptor implements IShaderDescriptor {

    private String frag, vert;

    @Override
    public String name() {
        return "Simple Depth";
    }

    @Override
    public String desc() {
        return "Creates a depthmap originating from the camera.";
    }

    @Override
    public String frag() {
        if(frag == null) {
//            frag = Gdx.files.classpath("net/ncguy/argent/editor/swing/shader/internal/simpledepth/SimpleDepth.frag").readString();
            frag = Reference.Defaults.Shaders.FRAGMENT;
        }
        return frag;
    }

    @Override
    public String vert() {
        if(vert == null) {
//            vert = Gdx.files.classpath("net/ncguy/argent/editor/swing/shader/internal/simpledepth/SimpleDepth.vert").readString();
            vert = Reference.Defaults.Shaders.VERTEX;
        }
        return vert;
    }

    @Override
    public BufferRenderer<?> constructRenderer(GameWorld.Generic<?> gameWorld) {
        return new DepthRenderer<>(gameWorld.renderer());
    }
}
