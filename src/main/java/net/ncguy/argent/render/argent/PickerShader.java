package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import net.ncguy.argent.utils.AppUtils;

import static com.badlogic.gdx.graphics.GL30.GL_BACK;
import static com.badlogic.gdx.graphics.GL30.GL_LEQUAL;

/**
 * Created by Guy on 30/07/2016.
 */
public class PickerShader extends BaseShader {

    public static final String SHADER_PREFIX = "picker";

    public PickerShader() {
        super();
        program = AppUtils.Shader.loadShader(SHADER_PREFIX);
        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);

        register(new Uniform("u_colour"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                Color col = AppUtils.Shader.bindPickerAttribute(combinedAttributes);
                System.out.println(col);
                shader.set(inputID, col);
            }
        });
    }

    @Override public void init() { super.init(program, null); }

    @Override public int compareTo(Shader other) { return 0; }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        this.context.setCullFace(GL_BACK);
        this.context.setDepthTest(GL_LEQUAL, 0, 1);
        this.context.setDepthMask(true);
    }

}

