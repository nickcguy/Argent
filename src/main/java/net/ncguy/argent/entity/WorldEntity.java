package net.ncguy.argent.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;
import net.ncguy.argent.entity.components.RenderableComponent;
import net.ncguy.argent.entity.components.TransformComponent;

import java.util.List;

/**
 * Created by Guy on 15/07/2016.
 */
public class WorldEntity extends Entity implements RenderableProvider, IConfigurable {

    private TransformComponent transform;

    public WorldEntity() {
        add(this.transform = new TransformComponent());
        this.transform.parent(this);
    }

    public TransformComponent transformComponent() { return transform; }
    public Matrix4 transform() { return transform.transform; }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        getComponents().forEach(c -> {
            if(c instanceof RenderableProvider)
                ((RenderableComponent)c).getRenderables(renderables, pool);
        });
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        getComponents().forEach(c -> {
            if(c instanceof IConfigurable)
                ((IConfigurable)c).getConfigurableAttributes(attrs);
        });
    }

    @Override
    public String toString() {
        if(EntityMappers.nameMapper.has(this)) {
            String name = EntityMappers.nameMapper.get(this).name;
            if(name.length() > 0) return name;
        }
        return super.toString();
    }
}
