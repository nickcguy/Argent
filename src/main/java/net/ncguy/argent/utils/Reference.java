package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.Matrix4;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 22/06/2016.
 */
public class Reference {

    public static class Matrix4Alias {

        public static final int TranslationX = Matrix4.M03;
        public static final int TranslationY = Matrix4.M13;
        public static final int TranslationZ = Matrix4.M23;

        public static final int ScaleX = Matrix4.M00;
        public static final int ScaleY = Matrix4.M11;
        public static final int ScaleZ = Matrix4.M22;

    }

    public static class Defaults {
        public static class Models {
            private static Material defaultMaterial;

            public static final Material material() {
                if (defaultMaterial == null) {
                    defaultMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE), new BlendingAttribute());
                    defaultMaterial.set(TextureAttribute.createDiffuse(SpriteCache.pixel()));
                    defaultMaterial.set(TextureAttribute.createNormal(SpriteCache.pixel()));
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Diffuse)).textureDescription.uWrap = Texture.TextureWrap.Repeat;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Diffuse)).textureDescription.vWrap = Texture.TextureWrap.Repeat;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Diffuse)).textureDescription.minFilter = Texture.TextureFilter.Linear;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Diffuse)).textureDescription.magFilter = Texture.TextureFilter.Linear;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Normal)).textureDescription.uWrap = Texture.TextureWrap.Repeat;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Normal)).textureDescription.vWrap = Texture.TextureWrap.Repeat;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Normal)).textureDescription.minFilter = Texture.TextureFilter.Linear;
                    ((TextureAttribute)defaultMaterial.get(TextureAttribute.Normal)).textureDescription.magFilter = Texture.TextureFilter.Linear;
                }
                return defaultMaterial;
            }
        }
        public static class Shaders {
            public static final String VERTEX = ""+
                    "#version 120\n" +
                    "\n" +
                    "attribute vec4 a_position;\n" +
                    "\n" +
                    "uniform mat4 u_projViewTrans;\n" +
                    "uniform mat4 u_worldTrans;\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tgl_Position = u_projViewTrans * (u_worldTrans * a_position)\n" +
                    "}\n";
            public static final String FRAGMENT = "" +
                    "#version 120\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tgl_FragColor = vec4(1.0);\n" +
                    "}\n";

            private static Map<BaseShader.Uniform, BaseShader.Setter> UNIFORMS;
            public static final Map<BaseShader.Uniform, BaseShader.Setter> UNIFORMS() {
                if(UNIFORMS == null) {
                    Map<BaseShader.Uniform, BaseShader.Setter> m = new HashMap<>();
//                    m.put(Inputs.projTrans,         Setters.projTrans);
//                    m.put(Inputs.viewTrans,         Setters.viewTrans);
//                    m.put(Inputs.projViewTrans,     Setters.projViewTrans);
//                    m.put(Inputs.cameraPosition,    Setters.cameraPosition);
//                    m.put(Inputs.)

                    Field[] fields = DefaultShader.Inputs.class.getDeclaredFields();
                    for (Field field : fields) {
                        try {
                            String fName = field.getName();
                            Field f = DefaultShader.Setters.class.getField(fName);
                            if(f == null) continue;
                            BaseShader.Uniform uniform = (BaseShader.Uniform) field.get(null);
                            BaseShader.Setter setter = (BaseShader.Setter)f.get(null);
                            m.put(uniform, setter);
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    UNIFORMS = m;
                }
                return UNIFORMS;
            }
        }
    }

}
