package net.ncguy.argent.editor.wrapper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.swing.config.ConfigControl;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.SpriteCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 09/07/2016.
 */
public class MaterialWrapper implements IConfigurable {

    public Material mtl;

    public MaterialWrapper(Material mtl) {
        this.mtl = mtl;
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        attr(attrs, "ID", () -> mtl.id, (var) -> mtl.id = var, ConfigControl.TEXTFIELD);
        // Blending attribute
        ConfigurableAttribute blendingAttr = attr(attrs, new Meta.Object("Alpha", "Blending"), () -> {
            BlendingAttribute attr = getBlendingAttribute();
            if(attr == null) return 1f;
            return attr.opacity;
        }, (val) -> {
            BlendingAttribute attr = getBlendingAttribute();
            if(attr != null)
                attr.opacity = val;
        }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        blendingAttr.addParam("min", Float.class, 0);
        blendingAttr.addParam("max", Float.class, 1);
        blendingAttr.addParam("precision", Integer.class, 2);
        // Texture Attributes
        generateAttrs(attrs, TextureAttribute.DiffuseAlias, TextureAttribute.Diffuse);
        generateAttrs(attrs, TextureAttribute.SpecularAlias, TextureAttribute.Specular);
        generateAttrs(attrs, TextureAttribute.BumpAlias, TextureAttribute.Bump);
        generateAttrs(attrs, TextureAttribute.NormalAlias, TextureAttribute.Normal);
        generateAttrs(attrs, TextureAttribute.AmbientAlias, TextureAttribute.Ambient);
        generateAttrs(attrs, TextureAttribute.EmissiveAlias, TextureAttribute.Emissive);
        generateAttrs(attrs, TextureAttribute.ReflectionAlias, TextureAttribute.Reflection);

        return attrs;
    }

    private <T extends Attribute> T getAttribute(long type, Class<T> cls) {
        Attribute attr = mtl.get(type);
        if(attr == null) {
            Attribute a = createAttribute(type);
            if(a == null) return null;
            mtl.set(a);
        }
        return cls.cast(attr);
    }

    // COLOUR ATTRIBUTES

    private BlendingAttribute getBlendingAttribute() {
        return getAttribute(BlendingAttribute.Type, BlendingAttribute.class);
    }

    // TEXTURE ATTRIBUTES

    private TextureAttribute getTextureAttribute(long type) {
        return getAttribute(type, TextureAttribute.class);
    }

    private TextureAttribute getDiffuseAttribute() {
        return getTextureAttribute(TextureAttribute.Diffuse);
    }
    private TextureAttribute getSpecularAttribute() {
        return getTextureAttribute(TextureAttribute.Specular);
    }
    private TextureAttribute getBumpAttribute() {
        return getTextureAttribute(TextureAttribute.Bump);
    }
    private TextureAttribute getNormalAttribute() {
        return getTextureAttribute(TextureAttribute.Normal);
    }
    private TextureAttribute getAmbientAttribute() {
        return getTextureAttribute(TextureAttribute.Ambient);
    }
    private TextureAttribute getEmissiveAttribute() {
        return getTextureAttribute(TextureAttribute.Emissive);
    }
    private TextureAttribute getReflectionAttribute() {
        return getTextureAttribute(TextureAttribute.Reflection);
    }

    private void generateAttrs(List<ConfigurableAttribute<?>> attrs, String alias, long type) {
        ConfigurableAttribute texAttr = attr(attrs, new Meta.Object("Texture", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return "";
            return "";
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null)
                attr.textureDescription.texture = (Texture)val;
        }, ConfigControl.SELECTIONLIST, this::stringToTex);

        ConfigurableAttribute minFilterAttr = attr(attrs, new Meta.Object("Min Filter", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return "";
            return attr.textureDescription.minFilter;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null)
                attr.textureDescription.minFilter = (Texture.TextureFilter) val;
        }, ConfigControl.COMBOBOX, Texture.TextureFilter::valueOf);

        ConfigurableAttribute magFilterAttr = attr(attrs, new Meta.Object("Mag Filter", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return "";
            return attr.textureDescription.magFilter;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null)
                attr.textureDescription.magFilter = (Texture.TextureFilter) val;
        }, ConfigControl.COMBOBOX, Texture.TextureFilter::valueOf);

        ConfigurableAttribute uWrapAttr = attr(attrs, new Meta.Object("U Wrap", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return "";
            return attr.textureDescription.uWrap;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null)
                attr.textureDescription.uWrap = (Texture.TextureWrap) val;
        }, ConfigControl.COMBOBOX, Texture.TextureWrap::valueOf);

        ConfigurableAttribute vWrapAttr = attr(attrs, new Meta.Object("V Wrap", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return "";
            return attr.textureDescription.vWrap;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null)
                attr.textureDescription.vWrap = (Texture.TextureWrap) val;
        }, ConfigControl.COMBOBOX, Texture.TextureWrap::valueOf);

        String[] texRefs = Argent.content.getAllRefs(Texture.class);
        SearchableList.Item.Data[] texItems = new SearchableList.Item.Data[texRefs.length];
        int index = 0;
        for (String ref : texRefs)
            texItems[index++] = new SearchableList.Item.Data<>(new TextureRegionDrawable(new TextureRegion(Argent.content.get(ref, Texture.class))), ref, ref);

        texAttr.addParam("items", SearchableList.Item.Data[].class, texItems);
        minFilterAttr.addParam("items", Texture.TextureFilter[].class, Texture.TextureFilter.values());
        magFilterAttr.addParam("items", Texture.TextureFilter[].class, Texture.TextureFilter.values());
        uWrapAttr.addParam("items", Texture.TextureWrap[].class, Texture.TextureWrap.values());
        vWrapAttr.addParam("items", Texture.TextureWrap[].class, Texture.TextureWrap.values());

        /*
        public float offsetU = 0;
	public float offsetV = 0;
	public float scaleU = 1;
	public float scaleV = 1;
         */

        ConfigurableAttribute uOffsetAttr = attr(attrs, new Meta.Object("U Offset", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return 0;
            return attr.offsetU;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null) attr.offsetU = (Float)val;
        }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        ConfigurableAttribute vOffsetAttr = attr(attrs, new Meta.Object("V Offset", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return 0;
            return attr.offsetV;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null) attr.offsetV = (Float)val;
        }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        ConfigurableAttribute uScaleAttr = attr(attrs, new Meta.Object("U Scale", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return 0;
            return attr.scaleU;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null) attr.scaleU = (Float)val;
        }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        ConfigurableAttribute vScaleAttr = attr(attrs, new Meta.Object("V Scale", "Texture|"+alias), () -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr == null) return 0;
            return attr.scaleV;
        }, (val) -> {
            TextureAttribute attr = getTextureAttribute(type);
            if(attr != null) attr.scaleV= (Float)val;
        }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        uOffsetAttr.addParam("min", Float.class, 0);
        vOffsetAttr.addParam("min", Float.class, 0);
        uScaleAttr.addParam("min", Float.class, 0);
        vScaleAttr.addParam("min", Float.class, 0);
        uOffsetAttr.addParam("max", Float.class, 1);
        vOffsetAttr.addParam("max", Float.class, 1);
        uScaleAttr.addParam("max", Float.class, 1);
        vScaleAttr.addParam("max", Float.class, 1);
        uOffsetAttr.addParam("precision", Integer.class, 2);
        vOffsetAttr.addParam("precision", Integer.class, 2);
        uScaleAttr.addParam("precision", Integer.class, 2);
        vScaleAttr.addParam("precision", Integer.class, 2);

    }

    private Attribute createAttribute(long type) {
        if(type == BlendingAttribute.Type) return new BlendingAttribute();
        if(type == TextureAttribute.Diffuse) return TextureAttribute.createDiffuse(SpriteCache.pixel());
        if(type == TextureAttribute.Specular) return TextureAttribute.createSpecular(SpriteCache.pixel());
        if(type == TextureAttribute.Bump) return TextureAttribute.createBump(SpriteCache.pixel());
        if(type == TextureAttribute.Normal) return TextureAttribute.createNormal(SpriteCache.pixel());
        if(type == TextureAttribute.Ambient) return TextureAttribute.createAmbient(SpriteCache.pixel());
        if(type == TextureAttribute.Emissive) return TextureAttribute.createEmissive(SpriteCache.pixel());
        if(type == TextureAttribute.Reflection) return TextureAttribute.createReflection(SpriteCache.pixel());
        return null;
    }

    // Cast Tunnels

    private Texture stringToTex(String texRef) {
        return Argent.content.get(texRef, Texture.class);
    }

}
