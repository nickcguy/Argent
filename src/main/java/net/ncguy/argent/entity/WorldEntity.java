package net.ncguy.argent.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.entity.components.RenderableComponent;
import net.ncguy.argent.entity.components.TransformComponent;

/**
 * Created by Guy on 15/07/2016.
 */
public class WorldEntity extends Entity implements RenderableProvider {

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }

    public static class ComponentMappers {

        ComponentMapper<RenderableComponent> renderableMapper = ComponentMapper.getFor(RenderableComponent.class);
        ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    }

}
