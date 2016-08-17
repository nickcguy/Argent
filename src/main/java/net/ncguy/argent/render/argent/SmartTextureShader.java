package net.ncguy.argent.render.argent;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.utils.AppUtils;

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
        register(Inputs.cameraNearFar, Setters.cameraNearFar);

        registerTextureUniform(TextureAttribute.Diffuse);
        registerTextureUniform(TextureAttribute.Specular);
        registerTextureUniform(TextureAttribute.Normal);
        registerTextureUniform(TextureAttribute.Ambient);
        registerTextureUniform(TextureAttribute.Bump);
        registerTextureUniform(TextureAttribute.Emissive);
        registerTextureUniform(TextureAttribute.Reflection);

        registerColourUniform(ColorAttribute.Diffuse);
        registerColourUniform(ColorAttribute.Specular);
        registerColourUniform(ColorAttribute.Ambient);
        registerColourUniform(ColorAttribute.Emissive);
        registerColourUniform(ColorAttribute.Reflection);
        registerColourUniform(ColorAttribute.AmbientLight);
        registerColourUniform(ColorAttribute.Fog);

        register(new Uniform("u_pickerColour"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                Color col = AppUtils.Shader.bindPickerAttribute(combinedAttributes);
                shader.set(inputID, col);
            }
        });
    }

    public void registerColourUniform(long type) {
        String alias = ColorAttribute.getAttributeAlias(type);
        alias = alias.replace("Color", "");
        alias = (alias.charAt(0)+"").toUpperCase() + alias.substring(1).toLowerCase();
        String uniform = "u_smart"+alias+"_Col";
        register(new Uniform(uniform), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if(combinedAttributes.has(type)) {
                    ColorAttribute attr = (ColorAttribute) combinedAttributes.get(type);
                    shader.set(inputID, attr.color);
                    return;
                }
                shader.set(inputID, Color.WHITE);
            }
        });
    }

    public void registerTextureUniform(long type) {
        String alias = TextureAttribute.getAttributeAlias(type);
        alias = alias.replace("Texture", "");
        alias = (alias.charAt(0)+"").toUpperCase() + alias.substring(1).toLowerCase();
        String uniform = "u_smart"+alias;
        Vector2 offset = new Vector2();
        Vector2 scale = new Vector2();
        register(new Uniform(uniform), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, AppUtils.Shader.bindTextureAttribute(shader, combinedAttributes, type));
            }
        });
        register(new Uniform(uniform + "_Offset"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if(combinedAttributes.has(type)) {
                    TextureAttribute attr = (TextureAttribute) combinedAttributes.get(type);
                    offset.set(attr.offsetU, attr.offsetV);
                } else offset.set(0, 0);
                shader.set(inputID, offset);
            }
        });
        register(new Uniform(uniform + "_Scale"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if(combinedAttributes.has(type)) {
                    TextureAttribute attr = (TextureAttribute) combinedAttributes.get(type);
                    scale.set(attr.scaleU, attr.scaleV);
                }
                else scale.set(1, 1);
                shader.set(inputID, scale);
            }
        });
    }

    /**
     * Use {@link AppUtils.Shader::bindTextureAttribute}
     * @param shader
     * @param combinedAttributes
     * @param type
     * @return
     */
    @Deprecated
    public int bindTextureAttribute(BaseShader shader, Attributes combinedAttributes, long type) {
        return AppUtils.Shader.bindTextureAttribute(shader, combinedAttributes, type);
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
