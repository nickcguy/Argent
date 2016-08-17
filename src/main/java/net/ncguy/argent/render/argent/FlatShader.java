package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 31/07/2016.
 */
public class FlatShader extends BaseShader {

    private Renderable renderable;

    public FlatShader(Renderable renderable) {
        super();
        this.renderable = renderable;

        program = AppUtils.Shader.loadShader("flat");
        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
        register(new Uniform("u_smartDiffuseCol"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                Color colour;
                if(combinedAttributes.has(ColorAttribute.Diffuse)) {
                    colour = ((ColorAttribute)(combinedAttributes.get(ColorAttribute.Diffuse))).color;
                }else{
                    colour = Color.WHITE;
                }
                shader.set(inputID, colour);
            }
        });
    }

    @Override
    public void begin(final Camera camera, final RenderContext context)
    {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_ALWAYS);
        context.setCullFace(GL20.GL_BACK);

    }

    @Override
    public void render(final Renderable renderable)
    {
        if (!renderable.material.has(BlendingAttribute.Type))
        {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        else
        {
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        super.render(renderable);
    }

    @Override
    public void init()
    {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(final Shader other)
    {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance)
    {
        return true;
    }

    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes)  {
        try{
            super.render(renderable, combinedAttributes);
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
