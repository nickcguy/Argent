package net.ncguy.argent.entity.components.physics;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 18/09/2016.
 */
@ComponentData(name = "Physics")
public class PhysicsComponent implements ArgentComponent {

    public String identifier = "";
    public boolean composite = false;
    public PhysicsData data;

    WorldEntity entity;

    public PhysicsComponent(WorldEntity entity) {
        this.entity = entity;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.entity;
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

    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return null;
    }

    @Override
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {

    }
}
