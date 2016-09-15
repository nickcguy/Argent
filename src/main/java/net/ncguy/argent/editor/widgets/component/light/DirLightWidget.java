package net.ncguy.argent.editor.widgets.component.light;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.DirLightComponent;
import net.ncguy.argent.ui.widget.Vector3Selection;

/**
 * Created by Guy on 14/09/2016.
 */
public class DirLightWidget extends LightWidget<DirLightComponent> {

    Vector3Selection direction;

    public DirLightWidget(DirLightComponent component) {
        super(component, "Directional Light");
    }

    @Override
    protected void init() {
        super.init();
        direction = new Vector3Selection(true);
    }

    @Override
    protected void setupUI() {
        collapsibleContent.add(direction).padRight(5).padLeft(8).padBottom(4).colspan(2).growX().row();
        super.setupUI();
    }

    @Override
    protected void setupListeners() {
        super.setupListeners();
        direction.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(event instanceof Vector3Selection.DirectionChangeEvent)
                    component.setDirection(direction.getRawDirection());
             }
        });
    }

    @Override
    public void setValues(WorldEntity entity) {
        direction.set(component.getDirection());
        super.setValues(entity);
    }
}
