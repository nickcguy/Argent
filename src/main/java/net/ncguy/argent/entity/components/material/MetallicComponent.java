package net.ncguy.argent.entity.components.material;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.MetallicWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 23/12/2016.
 */
@ComponentData(name = "Metallic", limit = 1)
public class MetallicComponent implements ArgentComponent {

    public float metallic;
    WorldEntity entity;

    public MetallicComponent(WorldEntity entity) {
        this.entity = entity;
    }

    public float getMetallic() {
        return metallic;
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
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
        return MetallicWidget.class;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }
}
