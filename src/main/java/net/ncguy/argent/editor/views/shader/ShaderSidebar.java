package net.ncguy.argent.editor.views.shader;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.shader.GlobalShaderUpdateEvent;
import net.ncguy.argent.event.shader.NewShaderEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.AssetItem;
import net.ncguy.argent.ui.Icons;

/**
 * Created by Guy on 31/08/2016.
 */
public class ShaderSidebar extends Table implements GlobalShaderUpdateEvent.GlobalShaderUpdateListener, NewShaderEvent.NewShaderListener {

    Slider shaderItemScale;
    Button syncBtn;
    GridGroup shaderItemGroup;

    ShaderSidebarContextMenu contextMenu;

    @Inject
    ProjectManager manager;

    public ShaderSidebar() {
        super(VisUI.getSkin());
        Argent.event.register(this);
        ArgentInjector.inject(this);
        initUI();
    }

    protected void initUI() {

        setBackground("default-pane");

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

        add(shaderItemScale).growX().padRight(4).padLeft(4);
        add(syncBtn).size(32).row();
        add(shaderItemGroup).colspan(2).grow().row();

        contextMenu = new ShaderSidebarContextMenu();
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                contextMenu.showMenu(getStage(), pos.x, pos.y);
            }
        });

        update();
    }

    public void update() {
        shaderItemGroup.clearChildren();
        manager.current().shaders.forEach(this::addSingle);

    }

    public void addSingle(ArgShader shader) {
        if(shader == null) return;
        AssetItem<ArgShader> item = new AssetItem<>(shader);
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
}
