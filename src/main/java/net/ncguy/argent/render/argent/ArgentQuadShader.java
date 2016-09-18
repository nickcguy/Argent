package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.utils.AppUtils;

import java.util.function.Consumer;

import static com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Inputs;
import static com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Setters;

/**
 * Created by Guy on 10/09/2016.
 */
public class ArgentQuadShader extends BaseShader {

    public Renderable renderable;

    Consumer<Vector2> screenResFunction;
    Vector2 screenRes;

    public ArgentQuadShader(Renderable renderable, ShaderProgram quadProgram, Consumer<Vector2> screenResFunction) {
        this.renderable = renderable;
        this.program = quadProgram;
        this.screenResFunction = screenResFunction;
        this.screenRes = new Vector2();

        register(Inputs.worldTrans, Setters.worldTrans);
        register(Inputs.projViewTrans, Setters.projViewTrans);

        register(new Uniform("u_screenRes"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if(screenResFunction != null)
                    screenResFunction.accept(screenRes);
                else screenRes.set(AppUtils.Graphics.getScreenSize());
                shader.program.setUniformf("u_screenRes", screenRes.x, screenRes.y);
            }
        });
        register(new Uniform("u_exposure"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.program.setUniformf("u_exposure", GlobalSettings.exposure);
            }
        });
        register(new Uniform("u_gamma"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.program.setUniformf("u_gamma", GlobalSettings.gamma);
            }
        });
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL30.GL_LEQUAL);
        context.setCullFace(GL30.GL_BACK);
    }


    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(Shader shader) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return true;
    }
}
