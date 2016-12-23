package net.ncguy.argent.render.pbr.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.material.ColourComponent;
import net.ncguy.argent.entity.components.material.MetallicComponent;

/**
 * Created by Guy on 23/12/2016.
 */
public class PBR_SurfaceShader extends BaseShader {

    Renderable renderable;

    public PBR_SurfaceShader(Renderable renderable, ShaderProgram program) {
        this.renderable = renderable;
        this.program = program;

        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
        register(DefaultShader.Inputs.cameraNearFar, DefaultShader.Setters.cameraNearFar);
        register(DefaultShader.Inputs.cameraPosition, DefaultShader.Setters.cameraPosition);

        register(new Uniform("u_screenRes"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.program.setUniformf("u_screenRes", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        });

        register(new Uniform("albedo"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                Color c = new Color(1, 1, 1, 1);
                if(renderable.userData instanceof ArgentComponent) {
                    WorldEntity e = ((ArgentComponent) renderable.userData).getWorldEntity();
                    if (e.has(ColourComponent.class)) {
                        ColourComponent col = e.get(ColourComponent.class);
                        c.set(col.colour);
                    }
                }
                float[] val = new float[]{ c.r, c.g, c.b };
                shader.program.setUniform3fv("albedo", val, 0, val.length);
            }
        });
        register(new Uniform("metallic"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                float f = 0;
                if(renderable.userData instanceof ArgentComponent) {
                    WorldEntity e = ((ArgentComponent) renderable.userData).getWorldEntity();
                    if (e.has(MetallicComponent.class)) {
                        MetallicComponent met = e.get(MetallicComponent.class);
                        f = met.getMetallic();
                    }
                }
                shader.program.setUniformf("metallic", f);
            }
        });
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void render(Renderable renderable) {
        super.render(renderable);
    }
}