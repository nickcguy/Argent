package net.ncguy.argent.misc.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 18/09/2016.
 */
public class WireframeShader extends BaseShader {

    public Color wireColour;
    public final int u_worldTrans;
    public final int u_projViewTrans;
    public final int u_colour;

    public WireframeShader() {
        super();
        wireColour = new Color(0.0f, 0.714f, 0.586f, 1.0f);
        program = AppUtils.Shader.loadShader("utils/wireframe");
        u_worldTrans = register(DefaultShader.Inputs.worldTrans);
        u_projViewTrans = register(DefaultShader.Inputs.projViewTrans);
        u_colour = register("u_colour");

    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public int compareTo(Shader shader) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        this.context.setDepthTest(GL30.GL_LEQUAL, 0, 1);
        this.context.setDepthMask(true);

        program.begin();

        set(u_projViewTrans, camera.combined);
        set(u_colour, wireColour);

        AppUtils.GL.polygonMode_Line();
    }

    @Override
    public void render(Renderable renderable) {
        set(u_worldTrans, renderable.worldTransform);
        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        AppUtils.GL.polygonMode_Fill();
        program.end();
    }
}
