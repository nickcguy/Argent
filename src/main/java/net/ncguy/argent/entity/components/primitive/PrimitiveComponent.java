package net.ncguy.argent.entity.components.primitive;

import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.entity.components.RenderableComponent;

import java.util.List;

/**
 * Created by Guy on 23/07/2016.
 */
public abstract class PrimitiveComponent extends RenderableComponent {

    public PrimitiveComponent(String ref) {
        this.modelRef = ref;
    }

    public abstract Model buildModel();

    public void applyModel() {
        applyModel(buildModel());
    }
    public void applyModel(Model model) {
        setModel(model);
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        addRenderableTransformAttributes(this.modelRef, attrs);
    }
}
