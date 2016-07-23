package net.ncguy.argent.entity.components.factory.primitive;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.utils.ImmutableArray;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.RenderableComponent;
import net.ncguy.argent.entity.components.factory.ArgentComponentFactory;
import net.ncguy.argent.entity.components.primitive.CubeComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 23/07/2016.
 */
@Meta(displayName = "Cube renderable", category = "Component|Primitive")
public class CubeFactory extends ArgentComponentFactory<CubeComponent> {

    @Override
    public Class<CubeComponent> componentClass() {
        return CubeComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        ImmutableArray<Component> components = entity.getComponents();
        error(errors, "A renderable component is already attached to the entity", () -> {
            for (Component component : components) {
                if(component.getClass().isAssignableFrom(RenderableComponent.class)) return true;
                if(RenderableComponent.class.isAssignableFrom(component.getClass())) return true;
            }
            return false;
        });
        return errors;
    }

    @Override
    public String meta() {
        return "";
    }

    @Override
    public CubeComponent build(WorldEntity entity) {
        CubeComponent cubeComponent = new CubeComponent();
        cubeComponent.parent(entity);
        return cubeComponent;
    }
}
