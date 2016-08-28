package net.ncguy.argent.editor.views;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.views.material.AttrPane;
import net.ncguy.argent.editor.views.material.TexturePane;
import net.ncguy.argent.editor.widgets.RenderWidget;
import net.ncguy.argent.editor.widgets.sidebar.MaterialTab;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.model.ModelComponent;
import net.ncguy.argent.entity.components.model.primitive.PrimitiveCubeModelComponent;
import net.ncguy.argent.entity.components.model.primitive.PrimitivePlaneModelComponent;
import net.ncguy.argent.entity.components.model.primitive.PrimitiveSphereModelComponent;
import net.ncguy.argent.render.argent.ArgentRenderer;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 02/08/2016.
 */
public class MaterialViewer extends ViewTab {

    private Table table;
    private EditorUI editorUI;
    private MaterialTab materialTab;
    private RenderWidget widget3D;
    private GameWorld<WorldEntity> materialWorld;
    private TexturePane texturePane;
    private AttrPane attrPane;

    private ModelComponent previewMesh;
    private ArgMaterial selectedMtl;

    private SplitPane texCfgSplit;

    private WorldEntity previewEntity;

    public MaterialViewer(EditorUI editorUI) {
        super(false, false);
        this.editorUI = editorUI;
        this.table = new Table(VisUI.getSkin());
        this.materialTab = new MaterialTab();
        this.materialTab.getContentTable().setBackground("default-pane");
        materialWorld = new GameWorld<WorldEntity>() {
            @Override
            public WorldEntity buildInstance() {
                return new WorldEntity();
            }
        };

        ArgentRenderer renderer = new ArgentRenderer(materialWorld);
        previewEntity = new WorldEntity();
        previewEntity.add(previewMesh = new PrimitiveCubeModelComponent(previewEntity, selectedMtl = new ArgMaterial(new Material(), false)));
        materialWorld.addInstance(previewEntity);

        this.materialTab.setOnSelect(this::onSelect);

        widget3D = new RenderWidget(editorUI, renderer);

        texturePane = new TexturePane(editorUI);
        attrPane = new AttrPane(this, editorUI);

        PrimitivePlaneModelComponent primitivePlaneModelComponent = new PrimitivePlaneModelComponent(previewEntity);
        primitivePlaneModelComponent.setTwoSided(true);

        Table meshTable = new Table(VisUI.getSkin());
        meshTable.setBackground("default-pane");
        TextButton cubeBtn = new TextButton("Cube", VisUI.getSkin());
        TextButton sphereBtn = new TextButton("Sphere", VisUI.getSkin());
        TextButton planeBtn = new TextButton("Plane", VisUI.getSkin());
        cubeBtn.addListener(new SetModelListener(this, new PrimitiveCubeModelComponent(previewEntity)));
        sphereBtn.addListener(new SetModelListener(this, new PrimitiveSphereModelComponent(previewEntity)));
        planeBtn.addListener(new SetModelListener(this, primitivePlaneModelComponent));
        meshTable.add(cubeBtn).expand().fill().padRight(4);
        meshTable.add(planeBtn).expand().fill().padRight(4);
        meshTable.add(sphereBtn).expand().fill().row();


        texCfgSplit = new SplitPane(attrPane, texturePane, true, VisUI.getSkin());

        Table cfgTbl = new Table(VisUI.getSkin());
        cfgTbl.setBackground("menu-bg");
        cfgTbl.add(meshTable).top().expandX().fillX().padBottom(5).row();
        cfgTbl.add(new Separator()).expandX().fillX().padBottom(5).row();
        cfgTbl.add(texCfgSplit).top().expand().fill().padBottom(5).row();

        Table sidebarTable = new Table(VisUI.getSkin());
        sidebarTable.add(this.materialTab.getContentTable()).grow().row();

        TextButton addMaterialBtn = new TextButton("New Material", VisUI.getSkin());
        addMaterialBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorUI.projectManager.current().addMaterial(new ArgMaterial(new Material()));
            }
        });
        sidebarTable.add(addMaterialBtn).growX();

        this.table.add(sidebarTable).width(300).expandY().fillY();
        this.table.add(widget3D).expand().fill();
        this.table.add(cfgTbl).top().right().expandY().fillY().width(300).row();

        widget3D.toBack();
        this.materialTab.getContentTable().toFront();
        cfgTbl.toFront();
    }

    @Override
    public void onOpen() {
        editorUI.getInputManager().addProcessor(editorUI.getFreeCamController());
        editorUI.getFreeCamController().setCamera(this.widget3D.getRenderer().camera());
    }

    @Override
    public void onClose() {
        editorUI.getInputManager().removeProcessor(editorUI.getFreeCamController());
    }

    public void setPreviewModel(ModelComponent newComponent) {
        if(previewMesh != null) {
            if(previewMesh.getClass().equals(newComponent.getClass())) {
                editorUI.getToaster().info("Preview mesh is already of type "+newComponent.getClass().getSimpleName());
                return;
            }
            previewMesh.remove();
            previewMesh = null;
        }
        previewMesh = newComponent;
        previewEntity.add(previewMesh);
        if(selectedMtl != null)
            previewMesh.setMaterial(selectedMtl);
    }

    public void onSelect(ArgMaterial mtl) {
        if(mtl == null) editorUI.getToaster().error("Material is null");
        if(selectedMtl != null) selectedMtl.save();
        selectedMtl = mtl;
        previewMesh.setMaterial(mtl);
        this.attrPane.onMaterialSelect(mtl);
    }

    @Override
    public String getTabTitle() {
        return "Material";
    }

    @Override
    public Table getContentTable() {
        return table;
    }


    public static List<String> getAttributeAliases(ArgMaterial mtl) {
        return getAttributeAliases(mtl.getAsset());
    }
    public static List<String> getAttributeAliases(Material mtl) {
        List<String> aliases = new ArrayList<>();
        mtl.iterator().forEachRemaining(attr -> aliases.add(Attribute.getAttributeAlias(attr.type)));
        return aliases;
    }

    public ArgMaterial getArgMaterial() {
        return selectedMtl;
    }

    public static class SetModelListener extends ClickListener {
        private MaterialViewer viewer;
        private ModelComponent modelComponent;

        public SetModelListener(MaterialViewer viewer, ModelComponent modelComponent) {
            this.viewer = viewer;
            this.modelComponent = modelComponent;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            this.viewer.setPreviewModel(this.modelComponent);
        }
    }

}
