package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.wrapper.MaterialWrapper;
import net.ncguy.argent.ui.ArgentList;
import net.ncguy.argent.world.GameWorld;

import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class MaterialPanel<T> extends DataPanel<T> {


    public MaterialPanel(GameWorld.Generic<T> gameWorld) {
        super(gameWorld);
    }

    private ArgentList<Material> matList;
    private Material selectedMtl;

    @Override
    protected AbstractPanel ui() {
        matList = new ArgentList<>(skin);
        addActor(matList);
        return super.ui();
    }

    @Override
    protected AbstractPanel listeners() {
        return super.listeners();
    }

    @Override
    protected AbstractPanel select(T obj) {
        ModelInstance inst = gameWorld.renderer().getRenderable(obj);
        matList.clearItems();
        inst.materials.forEach(m -> matList.addItem(m, this::selectMtl));
        selectMtl(null);
        return this;
    }

    private void selectMtl(Material mtl) {
        configTable.clearChildren();
        selectedMtl = mtl;
        if(mtl == null) return;
        MaterialWrapper wrapper = new MaterialWrapper(mtl);
        List<ConfigurableAttribute<?>> attrs = wrapper.getConfigAttrs();
        attrs.forEach(ca -> {
            System.out.println(ca.displayName());
            Object compObj = builder.buildComponent(ca);
            if (compObj instanceof Actor) {
                float compWidth = getWidth()-192;
                configTable.add(new Label(ca.displayName(), skin)).width(192);
                configTable.add((Actor) compObj).width(compWidth-10);
                configTable.row();
            }
        });
    }

    private void reselectMtl() { selectMtl(selectedMtl); }

    @Override
    protected void resizeElements() {

        if(matList != null) {
            matList.setBounds(0, 0, getWidth()*0.15f, getHeight());
        }

        if(configScroller != null) {
            configScroller.setBounds(getWidth()*0.15f, 0, getWidth()*0.85f, getHeight());
        }

        if(configTable != null) {
            configTable.pack();
            configTable.setX(5);
            configTable.setWidth(configScroller.getWidth()-10);
            configTable.setY(configTable.getHeight());
            reselectMtl();
        }
    }
}
