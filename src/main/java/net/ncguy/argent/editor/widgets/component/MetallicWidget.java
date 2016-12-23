package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSlider;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.material.MetallicComponent;

/**
 * Created by Guy on 23/12/2016.
 */
public class MetallicWidget extends ComponentWidget<MetallicComponent> {

    VisSlider slider;

    public MetallicWidget(MetallicComponent component) {
        super(component, "Metallic");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        slider = new VisSlider(0, 100, 1, false);
        slider.setAnimateDuration(.1f);
    }

    protected void setupUI() {
        collapsibleContent.add("Metallic").padRight(5).padBottom(4).left();
        collapsibleContent.add(slider).padRight(5).padBottom(4).growX().row();
    }

    protected void setupListeners() {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                component.setMetallic(slider.getPercent());
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
//        component.setMetallic(slider.getVisualPercent());
    }

    @Override
    public void setValues(WorldEntity entity) {
        slider.setValue(component.getMetallic()*100);
    }
}
