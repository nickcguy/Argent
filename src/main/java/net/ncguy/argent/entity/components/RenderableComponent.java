package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 15/07/2016.
 */
public class RenderableComponent implements RenderableProvider, Component, IConfigurable {

    private ModelInstance instance;
    private String modelRef;

    public RenderableComponent(ModelInstance instance) {
        this.instance = instance;
    }

    public ModelInstance instance() { return instance; }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        instance().getRenderables(renderables, pool);
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        attr(attrs, new Meta.Object("Model Ref", "Components|Renderable"), this::modelRef, this::modelRef, ConfigControl.TEXTFIELD);
        return attrs;
    }

    public String modelRef() { return modelRef; }
    public void modelRef(String modelRef) { this.modelRef = modelRef; }
}
