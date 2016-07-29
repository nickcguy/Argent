package net.ncguy.argent.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.widgets.*;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.misc.FreeCamController;
import net.ncguy.argent.render.AbstractWorldRenderer;
import net.ncguy.argent.ui.Toaster;

/**
 * Created by Guy on 27/07/2016.
 */
public class EditorUI extends Stage {

    private Table root;
    private Toaster toaster;
    private ArgentMenuBar menuBar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private Sidebar sidebar;
    private Inspector inspector;

    private RenderWidget widget3D;

    @Inject
    protected FreeCamController freeCamController;

    public EditorUI() {
        super(new ScreenViewport());
        ArgentInjector.inject(this);
        toaster = new Toaster(this);
        root = new Table(VisUI.getSkin());
        root.setFillParent(true);
        root.align(Align.center | Align.left);
        addActor(root);

        // Row 1: Menu
        menuBar = new ArgentMenuBar(this);
        root.add(menuBar.getTable()).fillX().expandX().row();

        // Row 2: Toolbar

        // Row 3: Sidebar | viewport | inspector
        Table center = new Table(VisUI.getSkin());
        sidebar = new Sidebar(this);
        widget3D = new RenderWidget(this);
        inspector = new Inspector(this);
        center.add(sidebar).width(300).top().left().expandY().fillY();
        center.add(widget3D).pad(2).expand().fill();
        center.add(inspector).width(300).top().right().expandY().fillY();
        root.add(center).top().left().expand().fill().row();


        // Row 4: Status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(25).row();

        Argent.onResize(this::resize);
    }

    public FreeCamController getFreeCamController() { return freeCamController; }

    public void setRenderer(AbstractWorldRenderer renderer) {
        this.widget3D.setRenderer(renderer);
        this.freeCamController.setCamera(renderer.camera());
    }

    @Override
    public void act(float delta) {
        this.freeCamController.update();
        super.act(delta);
    }

    protected void resize(int w, int h) {
        getViewport().update(w, h, true);
    }

}
