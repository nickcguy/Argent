package net.ncguy.argent.editor.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.widgets.Inspector;
import net.ncguy.argent.editor.widgets.RenderWidget;
import net.ncguy.argent.editor.widgets.Sidebar;

/**
 * Created by Guy on 02/08/2016.
 */
public class SceneViewer extends ViewTab {

    protected Table table;

    protected EditorUI editorUI;

    private Sidebar sidebar;
    private Inspector inspector;
    private RenderWidget widget3D;

    public SceneViewer(EditorUI editorUI) {
        super(false, false);
        this.editorUI = editorUI;

        sidebar = new Sidebar(editorUI);
        widget3D = new RenderWidget(editorUI);
        inspector = new Inspector(editorUI);
        table = new Table(VisUI.getSkin());
        table.add(sidebar).width(300).top().left().expandY().fillY();
        table.add(widget3D).pad(2).expand().fill();
        table.add(inspector).width(300).top().right().padTop(4).padRight(4);

        this.widget3D.toBack();
        table.toBack();
    }

    @Override
    public void onOpen() {
        try {
            editorUI.getFreeCamController().setCamera(this.widget3D.getRenderer().camera());
        }catch (Exception e) {}
    }

    public RenderWidget getWidget3D() {
        return widget3D;
    }

    @Override
    public String getTabTitle() {
        return "Scene";
    }

    @Override
    public Table getContentTable() {
        return table;
    }
}
