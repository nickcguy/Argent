package net.ncguy.argent.entity.components.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.widgets.component.ColourWidget;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 23/12/2016.
 */
@ComponentData(name = "Colour", limit = 1)
public class ColourComponent implements ArgentComponent {

    public Color colour;
    WorldEntity entity;

    public ColourComponent(WorldEntity entity) {
        this.entity = entity;
        this.colour = new Color();
    }

    @Override
    public WorldEntity getWorldEntity() {
        return entity;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void setType(Type type) {

    }

    @Override
    public void remove() {
        getWorldEntity().remove(this);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return ColourWidget.class;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }
}
