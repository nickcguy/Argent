package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Inputs;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Setters;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.utils.TextureCache;

/**
 * Created by Guy on 23/07/2016.
 */
public class SmartTextureShader extends BaseShader {

    public Renderable renderable;

    @Override
    public void end()
    {
        super.end();
    }

    public SmartTextureShader(final Renderable renderable, final ShaderProgram shaderProgramModelBorder)  {
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        register(Inputs.worldTrans, Setters.worldTrans);
        register(Inputs.projViewTrans, Setters.projViewTrans);
        register(Inputs.normalMatrix, Setters.normalMatrix);
        register(new Uniform("u_smartDiffuse"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                int unit = -1;
                if(combinedAttributes.has(TextureAttribute.Diffuse)) {
                    unit = shader.context.textureBinder.bind(((TextureAttribute) (combinedAttributes
                            .get(TextureAttribute.Diffuse))).textureDescription);
                }else{
                    TextureDescriptor<Texture> descriptor = new TextureDescriptor<>(TextureCache.pixel(), Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                    unit = shader.context.textureBinder.bind(descriptor);
                }
                shader.set(inputID, unit);
            }
        });
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
        register(new Uniform("u_smartSpecular"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                int unit = -1;
                if(combinedAttributes.has(TextureAttribute.Specular)) {
                    unit = shader.context.textureBinder.bind(((TextureAttribute) (combinedAttributes
                            .get(TextureAttribute.Specular))).textureDescription);
                }else{
                    TextureDescriptor<Texture> descriptor = new TextureDescriptor<>(TextureCache.pixel(), Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                    unit = shader.context.textureBinder.bind(descriptor);
                }
                shader.set(inputID, unit);
            }
        });
        register(new Uniform("u_smartNormal"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                int unit = -1;
                if(combinedAttributes.has(TextureAttribute.Normal)) {
                    unit = shader.context.textureBinder.bind(((TextureAttribute) (combinedAttributes
                            .get(TextureAttribute.Normal))).textureDescription);
                }else{
                    TextureDescriptor<Texture> descriptor = new TextureDescriptor<>(TextureCache.pixel(), Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                    unit = shader.context.textureBinder.bind(descriptor);
                }
                shader.set(inputID, unit);
            }
        });
    }

    @Override
    public void begin(final Camera camera, final RenderContext context)
    {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
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
