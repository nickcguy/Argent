package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.List;

/**
 * Created by Guy on 22/07/2016.
 */
public class PrimitiveComponent implements RenderableProvider, Component, IConfigurable {


    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {

    }
}
