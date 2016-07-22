package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.TextureCache;

import java.util.List;

/**
 * Created by Guy on 15/07/2016.
 */
public class RenderableComponent implements RenderableProvider, Component, IConfigurable {

    private ModelInstance instance;
    private String modelRef;

    public RenderableComponent() {
        this(new ModelInstance(new Model()));
    }

    public RenderableComponent(ModelInstance instance) {
        this.instance = instance;
        this.modelRef = "N/A";
    }

    public RenderableComponent(String modelRef) {
        this.instance = new ModelInstance(new Model());
        this.modelRef = modelRef;
        setModel(modelRef);
    }

    public void setModel(String ref) {
        Model model = Argent.content.get(ref, Model.class);
        if(model == null) {
            this.modelRef = "N/A";
            return;
        }
        setModel(model);
        this.modelRef = ref;
    }

    public void setModel(Model model) {
        Material[] mtls = this.instance.materials.toArray(Material.class);

        this.instance.model.nodes.clear();
        this.instance.model.nodes.addAll(model.nodes);

        this.instance.nodes.clear();
        this.instance.nodes.addAll(model.nodes);

        setMaterials(mtls);
    }

    public void setMaterial(final int index, final Material mtl) {
        final int[] i = {0};
        this.instance.nodes.forEach(n -> n.parts.forEach(p -> {
            if(i[0] == index) p.material = mtl;
            i[0]++;
        }));
    }

    public void setMaterials(Material... mtls) {
        int index = 0;
        for (Material mtl : mtls)
            setMaterial(index++, mtl);
    }

    public ModelInstance instance() { return instance; }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        instance().getRenderables(renderables, pool);
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        ConfigurableAttribute<String> modelRefAttr = attr(attrs, new Meta.Object("Model Ref", "Components|Renderable"), this::modelRef, this::modelRef, ConfigControl.SELECTIONLIST);
        String[] refs = Argent.content.getAllRefs(Model.class);
        SearchableList.Item<String>[] items = new SearchableList.Item[refs.length];
        int index = 0;
        for (String ref : refs) {
            items[index++] = new SearchableList.Item<>(new TextureRegionDrawable(new TextureRegion(TextureCache.pixel())), ref, ref, ref);
        }
        modelRefAttr.addParam("items", SearchableList.Item[].class, items);
    }

    public String modelRef() { return this.modelRef; }
    public void modelRef(String modelRef) {
        setModel(modelRef);
    }


}
