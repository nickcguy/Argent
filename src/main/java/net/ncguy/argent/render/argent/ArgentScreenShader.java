package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.utils.AppUtils;

import java.util.function.Consumer;

/**
 * Created by Guy on 16/08/2016.
 */
public class ArgentScreenShader extends BaseShader {

    public Renderable renderable;
    Consumer<Vector2> screenResFunction;
    Vector2 screenRes;
    @Override
    public void end() {
        super.end();
    }

    public ArgentScreenShader(final Renderable renderable, final ShaderProgram shaderProgramModelBorder, Consumer<Vector2> screenResFunction) {
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        this.screenResFunction = screenResFunction;
        this.screenRes = new Vector2();
        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);

        register(new Uniform("u_time"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, AppUtils.GL.getTime());
            }
        });

        register(new Uniform("u_screenRes"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if(screenResFunction != null)
                    screenResFunction.accept(screenRes);
                else screenRes.set(AppUtils.Graphics.getScreenSize());
                shader.program.setUniformf("u_screenRes", screenRes.x, screenRes.y);
            }
        });
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);

    }

    @Override
    public void render(final Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        super.render(renderable);
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(final Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance) {
        return true;
    }

    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes) {
        try {
            super.render(renderable, combinedAttributes);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
