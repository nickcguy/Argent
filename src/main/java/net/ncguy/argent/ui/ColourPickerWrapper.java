package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import net.ncguy.argent.utils.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by Guy on 11/07/2016.
 */
public class ColourPickerWrapper {

    private static ColourPickerWrapper instance = new ColourPickerWrapper();

    public static ColourPickerWrapper instance() {
        return instance;
    }

    // TODO remove dependency on VisUI
    private ColorPicker picker;
    private Field colourField;

    private ColourPickerWrapper() {
        picker = new ColorPicker();
        try {
            colourField = BasicColorPicker.class.getDeclaredField("color");
            ReflectionUtils.setCompletelyAccessible(colourField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void colour(String colour) {
        try{
            colour(Color.valueOf(colour));
        }catch (Exception e) {
            colour(Color.BLACK);
        }
    }

    public void colour(Color colour) {
        picker.setColor(colour);
    }

    public Color colour() {
        if(colourField != null) {
            try {
                return (Color) colourField.get(picker.getPicker());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Color.BLACK;
    }

    public void open(Actor actor) {
        actor.getStage().addActor(picker.fadeIn());
    }

    public void setListener(ColorPickerListener listener) {
        picker.setListener(listener);
    }

}
