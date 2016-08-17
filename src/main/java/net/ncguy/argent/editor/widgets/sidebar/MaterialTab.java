package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.MaterialImportEvent;
import net.ncguy.argent.event.MaterialRefreshEvent;
import net.ncguy.argent.event.MaterialReloadEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.AssetItem;
import net.ncguy.argent.ui.CollapsibleGridGroup;

import java.util.function.Consumer;

/**
 * Created by Guy on 31/07/2016.
 */
public class MaterialTab extends Tab implements MaterialImportEvent.MaterialImportListener, MaterialRefreshEvent.MaterialRefreshListener, MaterialReloadEvent.MaterialReloadListener {

    private Table content;
    private GridGroup projectGrid;
    private GridGroup globalGrid;
    private Consumer<ArgMaterial> onSelect;

    @Inject
    private ProjectManager projectManager;

    public MaterialTab() {
        super(false, false);
        ArgentInjector.inject(this);
        Argent.event.register(this);
        content = new Table(VisUI.getSkin());
        content.align(Align.topLeft);
        projectGrid = new GridGroup(60, 4);
        projectGrid.setTouchable(Touchable.enabled);
        globalGrid = new GridGroup(60, 4);
        globalGrid.setTouchable(Touchable.enabled);

        CollapsibleGridGroup projectGridGroup = new CollapsibleGridGroup("Project", projectGrid);
        CollapsibleGridGroup globalGridGroup = new CollapsibleGridGroup("Global", globalGrid);

        content.add("Materials").left().pad(5).row();
        content.add(new Separator()).expandX().fillX().row();
        content.add(projectGridGroup).expandX().fillX().row();
        content.add(globalGridGroup).expandX().fillX().row();

//        content.add("Materials").left().pad(5).row();
//        content.add("Project").left().pad(5).row();
//        content.add(new Separator()).expandX().fillX().row();
//        content.add(projectParent).expandX().fillX().row();
//        content.add(new Separator()).expandX().fillX().row();
//        content.add("Global").left().pad(5).row();
//        content.add(new Separator()).expandX().fillX().row();
//        content.add(globalParent).expandX().fillX().row();
//        content.add(new Separator()).expandX().fillX().row();

        reloadMaterials();
    }

    public Consumer<ArgMaterial> getOnSelect() { return onSelect; }
    public void setOnSelect(Consumer<ArgMaterial> onSelect) { this.onSelect = onSelect; }
    public void onSelect(ArgMaterial mtl) {
        if(onSelect != null) onSelect.accept(mtl);
    }

    private void reloadMaterials() {
        new MaterialReloadEvent().fire();
    }

    private void addLocalMtlItem(ArgMaterial mtl) {
        projectGrid.addActor(genItem(mtl));
    }
    private void addGlobalMtlItem(ArgMaterial mtl) {
        globalGrid.addActor(genItem(mtl));
    }

    private AssetItem<ArgMaterial> genItem(ArgMaterial mtl) {
        AssetItem<ArgMaterial> item = new AssetItem<>(mtl);
        item.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(getTapCount() >= 2)
                    onSelect(item.getAsset());
            }
        });
        return item;
    }

    @Override
    public String getTabTitle() {
        return "Materials";
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void onMaterialImport(MaterialImportEvent event) {
        addLocalMtlItem(event.getMtl());
    }

    @Override
    public void onMaterialRefresh(MaterialRefreshEvent event) {
        reloadMaterials();
    }

    @Override
    public void onMaterialReload(MaterialReloadEvent event) {
        projectGrid.clearChildren();
        globalGrid.clearChildren();
        projectManager.global().mtls.forEach(this::addGlobalMtlItem);
    }
}
