package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.wrapper.MaterialWrapper;
import net.ncguy.argent.ui.ArgentList;
import net.ncguy.argent.world.GameWorld;

import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class MaterialPanel<T> extends DataPanel<T> {


    public MaterialPanel(Stage stage, GameWorld.Generic<T> gameWorld) {
        super(stage, gameWorld);
    }

    private ArgentList<Material> matList;
    private Material selectedMtl;
    private TextButton addMtlBtn;
    private TextButton delMtlBtn;

    @Override
    protected AbstractPanel ui() {
        matList = new ArgentList<>(skin);
        addActor(matList);
        addMtlBtn = new TextButton("Add Material", skin);
        delMtlBtn = new TextButton("Delete Material", skin);

        addActor(addMtlBtn);
        addActor(delMtlBtn);
        return super.ui();
    }

    @Override
    protected AbstractPanel listeners() {
        addMtlBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newIndex = matList.getItems().size;
                NodePart part = getPart(newIndex);
                Material newMtl = new Material();
                ModelInstance inst = gameWorld.renderer().getRenderable(gameWorld.selected());
                inst.materials.add(newMtl);
                if(part == null) {
                    Argent.toast("Material Error", "No available node parts at index "+newIndex);
                    return;
                }else{
                    part.material = newMtl;
                }
                reselect();
                selectMtl(newMtl);
            }
        });
        delMtlBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newIndex = matList.getSelectedIndex();
                NodePart part = getPart(newIndex);
                if(part == null) {
                    Argent.toast("Material Error", "No node part at index "+newIndex);
                }else{
                    part.material = null;
                }
                ModelInstance inst = gameWorld.renderer().getRenderable(gameWorld.selected());
                inst.materials.removeValue(selectedMtl, true);
                reselect();
            }
        });
        return super.listeners();
    }

    @Override
    protected AbstractPanel select(T obj) {
        super.select(obj);
        if(obj == null) {
            matList.clearItems();
            selectMtl(null);
            return this;
        }
        ModelInstance inst = gameWorld.renderer().getRenderable(obj);
        matList.clearItems();
        inst.nodes.forEach(n -> n.parts.forEach(p -> {
            ArgentList.ArgentListElement<Material> e = new ArgentList.ArgentListElement<>(p.material, this::selectMtl);
            e.getString = () -> String.format("[%s] %s", n.id, e.obj.id);
            matList.addItem(e);
        }));
//        inst.materials.forEach(m -> {
//            ArgentList.ArgentListElement<Material> e = new ArgentList.ArgentListElement<>(m, this::selectMtl);
//            e.getString = () -> e.obj.id;
//            matList.addItem(e);
//        });

        selectMtl(null);
        return this;
    }

    private void selectMtl(Material mtl) {
        configTree.clearChildren();
        selectedMtl = mtl;
        if(mtl == null) return;
        MaterialWrapper wrapper = new MaterialWrapper(mtl);
        List<ConfigurableAttribute<?>> attrs = wrapper.getConfigAttrs();
        builder.compileSet(configTree, attrs);
        configTree.getRootNodes().forEach(node -> node.setExpanded(true));
//        attrs.forEach(ca -> {
//            System.out.println(ca.displayName());
//            Object compObj = builder.buildComponent(ca);
//            if (compObj instanceof Actor) {
//                float compWidth = getWidth()-192;
//                configTable.add(new Label(ca.displayName(), skin)).width(192);
//                configTable.add((Actor) compObj).width(compWidth-10);
//                configTable.row();
//            }
//        });
    }

    private NodePart getPart(int index) {
        ModelInstance inst = gameWorld.renderer().getRenderable(selected());
        int i = 0;
        for (Node node : inst.nodes) {
            for (NodePart part : node.parts) {
                if(i == index) return part;
                i++;
            }
        }
        return null;
    }

    private void reselectMtl() { selectMtl(selectedMtl); }

    @Override
    protected void resizeElements() {

        if(matList != null) {
            matList.setBounds(0, 70, getWidth()*0.15f, getHeight()-70);
        }

        if(addMtlBtn != null && delMtlBtn != null) {
            delMtlBtn.setBounds(2, 2, getWidth()*0.15f, 30);
            addMtlBtn.setBounds(2, 34, getWidth()*0.15f, 30);
        }

        if(configScroller != null) {
            configScroller.setBounds(getWidth()*0.15f, 0, getWidth()*0.85f, getHeight());
        }

        if(configTree != null) {
            configTree.pack();
            configTree.setX(5);
            configTree.setWidth(configScroller.getWidth()-10);
            configTree.setY(configTree.getHeight());
//            reselectMtl();
        }
    }
}
