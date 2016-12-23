package net.ncguy.argent.entity.components.physics;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DMass;

/**
 * Created by Guy on 27/10/2016.
 */
@ComponentData(name = "Ode Physics")
public class OdePhysicsComponent implements ArgentComponent {

    public String identifier = "";
    WorldEntity entity;

    DBody physicsBody;
    DMass physicsMass;

    public OdePhysicsComponent(WorldEntity entity) {
        this.entity = entity;
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

    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return null;
    }

    @Override
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {

    }
}
