package net.ncguy.argent.editor.views;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.views.shader.ShaderSidebar;
import net.ncguy.argent.event.shader.ShaderSelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.utils.InputManager;
import net.ncguy.argent.vpl.VPLContainer;
import net.ncguy.argent.vpl.VPLGraph;

/**
 * Created by Guy on 18/08/2016.
 */
public class ShaderViewer extends ViewTab implements ShaderSelectedEvent.ShaderSelectedListener {

    protected Table content;
    protected VPLContainer vplContainer;
    protected Label zoomLabel, camLabel;
    protected Table statusTable;

    protected ShaderSidebar sidebar;

    @Inject
    InputManager inputManager;
    private EditorUI editorUI;

    public ShaderViewer(EditorUI editorUI) {
        super(false, false);
        ArgentInjector.inject(this);
        Argent.event.register(this);
        this.editorUI = editorUI;
        content = new Table(VisUI.getSkin());
        statusTable = new Table(VisUI.getSkin());
        statusTable.setBackground("menu-bg");
//        content.setBackground(Icons.Icon.WARNING.drawable());

        vplContainer = new VPLContainer("shader");

        camLabel = new Label("", VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                camLabel.setText("Camera Position: "+vplContainer.getPosition().toString());
                super.act(delta);
            }
        };
        statusTable.add(camLabel).left();
//        vplContainer.setFillParent(true);

        sidebar = new ShaderSidebar();
        sidebar.toFront();
        sidebar.setTouchable(Touchable.enabled);

        content.add(sidebar).growY().width(384);
        content.add(vplContainer).grow().row();
    }

    @Override
    public void onOpen() {
        content.setDebug(false, true);
        vplContainer.setDebug(false, true);
    }

    @Override
    public void onClose() {
    }

    @Override
    public String getTabTitle() {
        return "Shaders";
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void onShaderSeleced(ShaderSelectedEvent event) {
        VPLGraph graph = vplContainer.pane.graph;
        ArgShader shader = event.shader;
        if(shader != null) {
            vplContainer.pane.switchGraph(shader.graph);
        }else{
            editorUI.getToaster().info("No shader selected");
        }
    }
}
