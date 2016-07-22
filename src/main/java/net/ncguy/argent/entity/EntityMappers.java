package net.ncguy.argent.entity;

import com.badlogic.ashley.core.ComponentMapper;
import net.ncguy.argent.entity.components.NameComponent;
import net.ncguy.argent.entity.components.RenderableComponent;
import net.ncguy.argent.entity.components.TransformComponent;

/**
 * Created by Guy on 21/07/2016.
 */
public class EntityMappers {
    public static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    public static final ComponentMapper<RenderableComponent> renderableMapper = ComponentMapper.getFor(RenderableComponent.class);
    public static final ComponentMapper<NameComponent> nameMapper = ComponentMapper.getFor(NameComponent.class);
}
