package net.ncguy.argent.vpl;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.views.ViewTab;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.utils.InputManager;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLViewer extends ViewTab {

    protected Table content;
    protected VPLContainer vplContainer;
    protected Label zoomLabel, camLabel;
    protected Table statusTable;

    @Inject
    InputManager inputManager;

    public VPLViewer(EditorUI editorUI) {
        super(false, false);
        ArgentInjector.inject(this);
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
        vplContainer.setFillParent(true);

//        content.add(statusTable).expandX().fillX().left().row();
        content.add(vplContainer).expand().fill().row();
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
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
