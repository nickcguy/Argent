package net.ncguy.argent.editor.views.material.attributes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.editor.views.material.AttrWidget;
import net.ncguy.argent.ui.EnumSelectionWithLabel;
import net.ncguy.argent.ui.FloatFieldWithLabel;
import net.ncguy.argent.ui.IntFieldWithLabel;
import net.ncguy.argent.ui.dnd.ImagedDropZone;

/**
 * Created by Guy on 04/08/2016.
 */
public class TextureAttrWidget extends AttrWidget<TextureAttribute> {

    FloatFieldWithLabel offsetU, offsetV;
    FloatFieldWithLabel scaleU, scaleV;
    IntFieldWithLabel uvIndex;
    EnumSelectionWithLabel<Texture.TextureFilter> minFilter, magFilter;
    EnumSelectionWithLabel<Texture.TextureWrap> uWrap, vWrap;
    ImagedDropZone<ArgTexture> texture;

    public TextureAttrWidget(Material mtl, TextureAttribute attribute) {
        super(mtl, attribute);
    }

    @Override
    protected void setupUI() {
        int size = -1;
        offsetU = new FloatFieldWithLabel("U", size, true);
        offsetV = new FloatFieldWithLabel("V", size, true);
        scaleU = new FloatFieldWithLabel("U", size, true);
        scaleV = new FloatFieldWithLabel("V", size, true);
        uvIndex = new IntFieldWithLabel("", size, false);
        minFilter = new EnumSelectionWithLabel<>("min", size, Texture.TextureFilter.class);
        magFilter = new EnumSelectionWithLabel<>("mag", size, Texture.TextureFilter.class);
        uWrap = new EnumSelectionWithLabel<>("min", size, Texture.TextureWrap.class);
        vWrap = new EnumSelectionWithLabel<>("mag", size, Texture.TextureWrap.class);
        texture = new ImagedDropZone<>(ArgTexture.class, "tex");

        int pad = 4;
        content.add("Offset").padRight(5).padBottom(pad).left().row();
        content.add(offsetU).padBottom(pad).left().padLeft(4);
        content.add(offsetV).padBottom(pad).left().padLeft(4).row();

        content.add("Scale").padRight(5).padBottom(pad).left().row();
        content.add(scaleU).padBottom(pad).left().padLeft(4);
        content.add(scaleV).padBottom(pad).left().padLeft(4).row();

        content.add("uvIndex").padRight(5).padBottom(pad).left().row();
        content.add(uvIndex).padBottom(pad).expandX().fillX().left().padLeft(4).row();

        content.add("Filter").padRight(5).padBottom(pad).left().row();
        content.add(minFilter).padBottom(pad).left().padLeft(4).row();
        content.add(magFilter).padBottom(pad).left().padLeft(4).row();

        content.add("Wrap").padRight(5).padBottom(pad).left().row();
        content.add(uWrap).padBottom(pad).left().padLeft(4).row();
        content.add(vWrap).padBottom(pad).left().padLeft(4).row();

        content.add("Texture").padRight(5).padBottom(pad).left().row();
        content.add(texture).padBottom(pad).expandX().fillX().size(48).padLeft(4).row();
    }

    @Override
    protected void setupListeners() {
        
        offsetU.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.offsetU = offsetU.getFloat();
            }
        });
        offsetV.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.offsetV = offsetV.getFloat();
            }
        });
        scaleU.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.scaleU = scaleU.getFloat();
            }
        });
        scaleV.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.scaleV = scaleV.getFloat();
            }
        });
        uvIndex.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.uvIndex = uvIndex.getInt();
            }
        });
        minFilter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.textureDescription.minFilter = minFilter.getSelected();
            }
        });
        magFilter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.textureDescription.magFilter = magFilter.getSelected();
            }
        });
        uWrap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.textureDescription.uWrap = uWrap.getSelected();
            }
        });
        vWrap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attribute.textureDescription.vWrap = vWrap.getSelected();
            }
        });
        
        texture.setOnDrop(tex -> {
            texture.setImage(tex.icon());
            attribute.textureDescription.texture = tex.getTexture();
        });
    }

    @Override
    public void setValues() {
        offsetU.setText(String.valueOf(attribute.offsetU));
        offsetV.setText(String.valueOf(attribute.offsetV));
        scaleU.setText(String.valueOf(attribute.scaleU));
        scaleV.setText(String.valueOf(attribute.scaleV));
        uvIndex.setText(String.valueOf(attribute.uvIndex));
        minFilter.setSelected(attribute.textureDescription.minFilter);
        magFilter.setSelected(attribute.textureDescription.magFilter);
        uWrap.setSelected(attribute.textureDescription.uWrap);
        vWrap.setSelected(attribute.textureDescription.vWrap);

        if(attribute.textureDescription.texture == null) texture.setImage(null);
        else texture.setImage(new TextureRegionDrawable(new TextureRegion(attribute.textureDescription.texture)));
    }



}
