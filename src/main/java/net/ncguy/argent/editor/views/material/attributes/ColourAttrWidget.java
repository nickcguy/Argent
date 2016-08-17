package net.ncguy.argent.editor.views.material.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import net.ncguy.argent.editor.views.material.AttrWidget;
import net.ncguy.argent.ui.ColourPickerWrapper;
import net.ncguy.argent.utils.TextureCache;

/**
 * Created by Guy on 16/08/2016.
 */
public class ColourAttrWidget extends AttrWidget<ColorAttribute> {

    TextButton btn;
    Image img;
    ColourPickerWrapper wrapper;

    public ColourAttrWidget(Material mtl, ColorAttribute attribute) {
        super(mtl, attribute);
        wrapper = ColourPickerWrapper.instance();
    }

    @Override
    protected void setupUI() {
        btn = new TextButton("#"+attribute.color.toString(), VisUI.getSkin());
        img = new Image(TextureCache.white());

        content.add(img).expandX().fillX().height(32).row();
        content.add(btn).expandX().fillX().height(32).row();
    }

    @Override
    protected void setupListeners() {
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                wrapper.editAlpha(false);
                attribute.color.a = 1.0f;
                wrapper.colour(attribute.color);
                wrapper.setListener(new ColorPickerListener() {
                    @Override public void canceled(Color oldColor) {
                        oldColor.a = 1.0f;
                        attribute.color.set(oldColor);
                        img.setColor(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        newColor.a = 1.0f;
                        attribute.color.set(newColor);
                        img.setColor(newColor);
                    }

                    @Override
                    public void reset(Color previousColor, Color newColor) {

                    }

                    @Override
                    public void finished(Color newColor) {
                        newColor.a = 1.0f;
                        btn.setText("#"+newColor.toString());
                    }
                });
                wrapper.open(btn);
            }
        });
    }

    @Override
    protected void setValues() {
        btn.setText("#"+attribute.color.toString());
    }
}
