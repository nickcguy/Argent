package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.MaterialReloadEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.AssetItem;
import net.ncguy.argent.ui.CollapsibleGridGroup;
import net.ncguy.argent.ui.Icons;

import java.util.function.Consumer;

/**
 * Created by Guy on 31/07/2016.
 */
public class ShaderTab extends Tab {

    private Table content;
    private GridGroup globalGrid;
    private Consumer<ArgShader> onSelect;

    private Slider zoomSlider;
    private Button syncBtn;

    @Inject
    private ProjectManager projectManager;

    public ShaderTab() {
        super(false, false);
        ArgentInjector.inject(this);
        Argent.event.register(this);
        content = new Table(VisUI.getSkin());
        content.align(Align.topLeft);
        globalGrid = new GridGroup(60, 4);
        globalGrid.setTouchable(Touchable.enabled);

        zoomSlider = new Slider(16, 256, 16, false, VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                super.act(delta);
                float v = zoomSlider.getVisualValue();
                if(globalGrid.getItemHeight() != v)
                    globalGrid.setItemSize(v);
            }
        };
        zoomSlider.setValue(64);
        zoomSlider.setAnimateDuration(.1f);

        syncBtn = new ImageButton(VisUI.getSkin());
        Image i = new Image(Icons.Icon.REFRESH.drawable());
        i.setSize(32, 32);
        syncBtn.addActor(i);
        syncBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reconstructShaderGrid();
            }
        });

        CollapsibleGridGroup globalGridGroup = new CollapsibleGridGroup("Shaders", globalGrid);

        content.add(zoomSlider).growX().padRight(4);
        content.add(syncBtn).size(32).row();
        content.add(globalGridGroup).growX().colspan(2).row();

        reloadMaterials();
    }

    public Consumer<ArgShader> getOnSelect() { return onSelect; }
    public void setOnSelect(Consumer<ArgShader> onSelect) { this.onSelect = onSelect; }
    public void onSelect(ArgShader mtl) {
        if(onSelect != null) onSelect.accept(mtl);
    }

    private void reloadMaterials() {
        new MaterialReloadEvent().fire();
    }


    private void addItem(ArgShader mtl) {
        globalGrid.addActor(genItem(mtl));
    }

    private AssetItem<ArgShader> genItem(ArgShader mtl) {
        AssetItem<ArgShader> item = new AssetItem<>(mtl);
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
        return "Shaders";
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    public void reconstructShaderGrid() {
        globalGrid.clearChildren();
        projectManager.current().shaders.forEach(this::addItem);
    }

}
