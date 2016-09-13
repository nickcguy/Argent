package net.ncguy.argent.editor.views.shader;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.shader.GlobalShaderUpdateEvent;
import net.ncguy.argent.event.shader.NewShaderEvent;
import net.ncguy.argent.event.shader.ShaderSelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.AssetItem;
import net.ncguy.argent.ui.Icons;
import net.ncguy.argent.ui.widget.CollectionWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 31/08/2016.
 */
public class ShaderSidebar extends Table implements GlobalShaderUpdateEvent.GlobalShaderUpdateListener, NewShaderEvent.NewShaderListener, ShaderSelectedEvent.ShaderSelectedListener {

    Table topTable;
    Slider shaderItemScale;
    Button syncBtn;
    GridGroup shaderItemGroup;

    ShaderSidebarContextMenu contextMenu;

    SplitPane splitPane;

    Table bottomTable;

    @Inject
    ProjectManager manager;

    Value varWidgetValue;
    CollectionWidget variableWidget;

    ArgShader selected;
    AssetItem<ArgShader> selectedItem;

    Map<ArgShader, AssetItem<ArgShader>> itemMap;

    public ShaderSidebar() {
        super(VisUI.getSkin());
        Argent.event.register(this);
        ArgentInjector.inject(this);
        itemMap = new HashMap<>();
        initUI();
    }

    protected void initUI() {

        topTable = new Table(VisUI.getSkin());

        topTable.setBackground("default-pane");

        shaderItemScale = new Slider(16, 256, 8, false, VisUI.getSkin());
        shaderItemScale.setAnimateDuration(.2f);

        syncBtn = new ImageButton(VisUI.getSkin());
        Image i = new Image(Icons.Icon.REFRESH.drawable());
        i.setSize(32, 32);
        syncBtn.addActor(i);
        syncBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                update();
            }
        });

        shaderItemGroup = new GridGroup(60, 4){
            @Override
            public void act(float delta) {
                setItemSize(shaderItemScale.getVisualValue());
                super.act(delta);
            }
        };

        topTable.add(shaderItemScale).growX().padRight(4).padLeft(4);
        topTable.add(syncBtn).size(32).row();
        topTable.add(shaderItemGroup).colspan(2).grow().row();

        contextMenu = new ShaderSidebarContextMenu();
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(event.isStopped()) return;
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                contextMenu.showMenu(getStage(), pos.x, pos.y);
            }
        });

        update();

        bottomTable = new Table(VisUI.getSkin());
        bottomTable.setBackground("default-pane");
        variableWidget = new CollectionWidget(false, table -> {
            table.add("Variables").padRight(4).left();
            table.add().growX();
            table.add("[+Var]").right().row();
        });

        varWidgetValue = new Value() {
            @Override
            public float get(Actor context) {
                return variableWidget.getWidth()/2;
            }
        };

        bottomTable.add(variableWidget).grow().row();

        splitPane = new SplitPane(topTable, bottomTable, true, VisUI.getSkin());
        splitPane.setMinSplitAmount(0.1f);
        splitPane.setMaxSplitAmount(0.9f);
        add(splitPane).grow().row();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);
    }

    public void addVariable(String name, Class<?> type) {
        if(!type.isPrimitive()) return;

        addVariableEntry(name, type);
    }
    public void addVariableEntry(String name, Class<?> type) {
        variableWidget.add(name).growX();
        variableWidget.add(type.getSimpleName()).growX().row();
    }

    public void update() {
        itemMap.clear();
        shaderItemGroup.clearChildren();
        manager.current().shaders.forEach(this::addSingle);

    }

    public void addSingle(ArgShader shader) {
        if(shader == null) return;
        AssetItem<ArgShader> item = new AssetItem<>(shader);
        item.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(getTapCount() == 2)
                    new ShaderSelectedEvent(shader).fire();
            }
        });
        itemMap.put(shader, item);
//        item.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                new ShaderSelectedEvent(item.getAsset()).fire();
//            }
//        });
        shaderItemGroup.addActor(item);
    }

    @Override
    public void onGlobalShaderUpdate(GlobalShaderUpdateEvent event) {
        update();
    }

    @Override
    public void onNewShader(NewShaderEvent event) {
        addSingle(event.shader);
    }

    @Override
    public void onShaderSeleced(ShaderSelectedEvent event) {
        selected = event.shader;
        if(!itemMap.containsKey(selected)) return;
        AssetItem<ArgShader> item = itemMap.get(selected);
        selectItem(item);
    }

    public void selectItem(AssetItem<ArgShader> item) {
        if(selectedItem != null)
            selectedItem.overrideIcon(null);
        selectedItem = item;
        selectedItem.overrideIcon(new NinePatchDrawable(Icons.Node.ASSET_BG_SELECTED.patch()));
    }

}
