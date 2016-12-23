package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSlider;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.material.ColourComponent;


/**
 * Created by Guy on 23/12/2016.
 */
public class ColourWidget extends ComponentWidget<ColourComponent> {

    VisSlider hSlider;
    VisSlider sSlider;
    VisSlider vSlider;

    public ColourWidget(ColourComponent component) {
        super(component, "Colour");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        hSlider = new VisSlider(0, 360, 1, false);
        sSlider = new VisSlider(0, 100, 1, false);
        vSlider = new VisSlider(0, 100, 1, false);

        hSlider.setAnimateDuration(.1f);
        sSlider.setAnimateDuration(.1f);
        vSlider.setAnimateDuration(.1f);
    }

    protected void setupUI() {
        collapsibleContent.add("HSV").padRight(5).padBottom(4).left().colspan(2).row();

        collapsibleContent.add("H").left().padRight(5).padBottom(4);
        collapsibleContent.add(hSlider).padRight(5).padBottom(4).growX().row();

        collapsibleContent.add("S").left().padRight(5).padBottom(4);
        collapsibleContent.add(sSlider).padRight(5).padBottom(4).growX().row();

        collapsibleContent.add("V").left().padRight(5).padBottom(4);
        collapsibleContent.add(vSlider).padRight(5).padBottom(4).growX().row();
    }

    protected void setupListeners() {
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                java.awt.Color jColour = java.awt.Color.getHSBColor(hSlider.getPercent(),
                        sSlider.getPercent(), vSlider.getPercent());
                component.colour.set(jColour.getRed()/255f, jColour.getGreen()/255f, jColour.getBlue()/255f, 1.0f);
            }
        };
        hSlider.addListener(changeListener);
        sSlider.addListener(changeListener);
        vSlider.addListener(changeListener);
    }

    @Override
    public void act(float delta) {
//        java.awt.Color jColour = java.awt.Color.getHSBColor(hSlider.getVisualPercent(),
//                sSlider.getVisualPercent(), vSlider.getVisualPercent());
//        component.colour.set(jColour.getRed()/255f, jColour.getGreen()/255f, jColour.getBlue()/255f, 1.0f);
    }

    @Override
    public void setValues(WorldEntity entity) {
        Color col = component.colour;
        float[] hsv = new float[]{
                0.0f, 0.0f, 0.0f
        };
        java.awt.Color.RGBtoHSB((int)(col.r*255), (int)(col.g*255), (int)(col.b*255), hsv);
        hSlider.setValue(hsv[0]);
        sSlider.setValue(hsv[1]);
        vSlider.setValue(hsv[2]);
    }
}
