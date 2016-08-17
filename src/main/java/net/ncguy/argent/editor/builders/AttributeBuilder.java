package net.ncguy.argent.editor.builders;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

/**
 * Created by Guy on 04/08/2016.
 */
public class AttributeBuilder {

    public static Attribute buildAttribute(long type) {
        if(TextureAttribute.is(type))
            return buildTextureAttribute(type);
        if(ColorAttribute.is(type))
            return buildColorAttribute(type);
        if(BlendingAttribute.is(type))
            return buildBlendingAttribute();
        return null;
    }

    public static TextureAttribute buildTextureAttribute(long type) {
        return new TextureAttribute(type);
    }

    public static ColorAttribute buildColorAttribute(long type) {
        return new ColorAttribute(type);
    }

    public static BlendingAttribute buildBlendingAttribute() {
        return new BlendingAttribute();
    }

}
