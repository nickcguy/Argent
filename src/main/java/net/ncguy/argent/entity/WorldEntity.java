package net.ncguy.argent.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;
import net.ncguy.argent.entity.components.RenderableComponent;
import net.ncguy.argent.entity.components.TransformComponent;

import java.util.ArrayList;
import java.util.List;

import static net.ncguy.argent.entity.WorldEntity.ComponentMappers.*;

/**
 * Created by Guy on 15/07/2016.
 */
public class WorldEntity extends Entity implements RenderableProvider, IConfigurable {

    public void invalidate() {
        RenderableComponent ren = renderableMapper.get(this);
        TransformComponent trans = transformMapper.get(this);
        if(ren != null && trans != null) {
            ren.instance().transform = trans.transform;
        }
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        RenderableComponent ren = renderableMapper.get(this);
        if(ren != null) ren.instance().getRenderables(renderables, pool);
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        getComponents().forEach(c -> {
            if(c instanceof IConfigurable)
                attrs.addAll(((IConfigurable) c).getConfigurableAttributes());
        });
        return attrs;
    }

    public static class ComponentMappers {

        public static final ComponentMapper<RenderableComponent> renderableMapper = ComponentMapper.getFor(RenderableComponent.class);
        public static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    }

}
