package net.ncguy.argent.misc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import net.ncguy.argent.entity.attributes.PickerIDAttribute;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 18/09/2016.
 */
public class PickerShader extends BaseShader {

    public final int u_worldTrans;
    public final int u_projViewTrans;
    public final int u_colour;

    public PickerShader() {
        super();
        program = AppUtils.Shader.loadShader("utils/picker");
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
        this.context.setDepthTest(GL30.GL_LEQUAL);
        this.context.setDepthMask(true);

        program.begin();
        set(u_projViewTrans, camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
        set(u_worldTrans, renderable.worldTransform);
        if(renderable.material.has(PickerIDAttribute.Type))
            set(u_colour, ((PickerIDAttribute)renderable.material.get(PickerIDAttribute.Type)).colour);
        else set(u_colour, Color.WHITE);
        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
    }
}
