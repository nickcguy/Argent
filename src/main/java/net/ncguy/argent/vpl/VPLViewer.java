package net.ncguy.argent.vpl;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.views.ViewTab;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLViewer extends ViewTab {

    protected Table content;
    protected VPLContainer vplContainer;
    protected VPLWidget widget;
    protected Label zoomLabel, camLabel;
    protected Table statusTable;

    public VPLViewer(EditorUI editorUI) {
        super(false, false);
        content = new Table(VisUI.getSkin());
        statusTable = new Table(VisUI.getSkin());
        statusTable.setBackground("menu-bg");
//        content.setBackground(Icons.Icon.WARNING.drawable());

        vplContainer = new VPLContainer();
        widget = new VPLWidget(editorUI, vplContainer);

        zoomLabel = new Label("", VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                zoomLabel.setText("Current Zoom: "+vplContainer.getZoom());
                super.act(delta);
            }
        };
        camLabel = new Label("", VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                camLabel.setText("Camera Position: "+vplContainer.getPosition().toString());
                super.act(delta);
            }
        };
        statusTable.add(zoomLabel).left();
        statusTable.add(new Separator()).expandY().fillY().padLeft(5).padRight(5);
        statusTable.add(camLabel).left();

        content.add(statusTable).expandX().fillX().left().row();
        content.add(widget).expand().fill().row();
    }

    @Override
    public String getTabTitle() {
        return "VPL";
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
