package net.ncguy.argent.misc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 18/09/2016.
 */
public class MaskedShader extends BaseShader {

    public final int u_worldTrans;
    public final int u_projViewTrans;
    public final int u_colour;
    public final int u_depthBuffer;

    public MaskedShader() {
        super();
        program = AppUtils.Shader.loadShader("utils/masked");
        u_worldTrans = register(DefaultShader.Inputs.worldTrans);
        u_projViewTrans = register(DefaultShader.Inputs.projViewTrans);
        u_colour = register("u_colour");
        u_depthBuffer = register("u_depthBuffer");
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
        this.context.setDepthTest(GL30.GL_LEQUAL, -1, 1);
        this.context.setDepthMask(true);

        program.begin();
        set(u_projViewTrans, camera.combined);
        if(Shaders.instance().depthFBOSupplier != null) {
            Shaders.instance().depthFBOSupplier.get().bind(1);
            set(u_depthBuffer, 1);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        }
    }

    @Override
    public void render(Renderable renderable) {
        set(u_worldTrans, renderable.worldTransform);
        if(renderable.material.has(ColorAttribute.Diffuse))
            set(u_colour, ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color);
        else set(u_colour, Color.WHITE);
        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
    }
}
