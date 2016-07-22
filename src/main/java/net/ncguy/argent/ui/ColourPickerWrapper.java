package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

/**
 * Created by Guy on 21/07/2016.
 */
public class ColourPickerWrapper {

    private static ColourPickerWrapper instance = new ColourPickerWrapper();
    public static ColourPickerWrapper instance() { return instance; }


    private ColorPicker picker;
    private ColourPickerWrapper() {
        picker = new ColorPicker();
    }


    public void colour(String colour) {
        try{
            colour(Color.valueOf(colour));
        }catch(Exception ignored) {
            colour(Color.BLACK);
        }
    }

    public void colour(Color colour) {
        picker.setColor(colour);
    }

    public void open(Actor actor) {
        actor.getStage().addActor(picker.fadeIn());
    }

    public void setListener(ColorPickerListener listener) {
        picker.setListener(listener);
    }

}
