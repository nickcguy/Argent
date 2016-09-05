package net.ncguy.argent.editor;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.ToolManager;
import net.ncguy.argent.editor.views.ViewerTabControl;
import net.ncguy.argent.editor.widgets.ArgentMenuBar;
import net.ncguy.argent.editor.widgets.DebugPreview;
import net.ncguy.argent.editor.widgets.StatusBar;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.injector.InjectionStore;
import net.ncguy.argent.misc.FreeCamController;
import net.ncguy.argent.render.AbstractWorldRenderer;
import net.ncguy.argent.ui.Toaster;
import net.ncguy.argent.utils.InputManager;

import static com.badlogic.gdx.Input.Buttons.MIDDLE;

/**
 * Created by Guy on 27/07/2016.
 */
public class EditorUI extends Stage {

    private Table root;
    private Toaster toaster;
    private ArgentMenuBar menuBar;
    private FileChooser fileChooser;
    private StatusBar statusBar;
    private ViewerTabControl viewer;


    private DebugPreview debug;

    @Inject
    public ToolManager toolManager;
    @Inject
    protected InputManager inputManager;
    @Inject
    protected FreeCamController freeCamController;
    @Inject
    public ProjectManager projectManager;

    private AbstractWorldRenderer renderer;

    public EditorUI() {
        super(new ScreenViewport());
        ArgentInjector.inject(this);
        toaster = new Toaster(this);
        try {
            InjectionStore.setGlobal(toaster);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Argent.onResize(this::resize);
        init();
    }

    public void init() {
        root = new Table(VisUI.getSkin());
        root.setFillParent(true);
        root.align(Align.center | Align.left);
        addActor(root);

        // Row 1: Menu
        menuBar = new ArgentMenuBar(this);
        root.add(menuBar.getTable()).fillX().expandX().row();

        // Row 2: Toolbar
//        root.add(debug = DebugPreview.instance()).row();
//        debug = DebugPreview.instance();
//        root.add(debug).row();

        // Row 3: Sidebar | viewport | inspector
        viewer = new ViewerTabControl(this);
        root.add(viewer).top().left().expand().fill().row();

        // Row 4: Status bar
        statusBar = new StatusBar();
        root.add(statusBar).expandX().fillX().height(25).row();


        setupInput();
    }

    public InputManager getInputManager() { return inputManager; }

    public void setupInput() {
        inputManager.clear();
        addListener(Argent.globalListener);
        inputManager.addProcessor(this);
        inputManager.addProcessor(new InputAdapter(){
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                unfocusAll();
                return false;
            }
        });
        inputManager.addProcessor(toolManager);
        inputManager.addProcessor(freeCamController);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == MIDDLE) focusOnEntity();
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void focusOnEntity() {
        WorldEntity e = projectManager.current().currScene.selected();
        if(e == null) {
            toaster.info("No entity selected");
            return;
        }
        toaster.info("Focusing on entity "+e.toString());
    }

    public FreeCamController getFreeCamController() { return freeCamController; }

    public AbstractWorldRenderer getRenderer() { return renderer;}

    public void setRenderer(AbstractWorldRenderer renderer) {
        this.renderer = renderer;

        this.viewer.getSceneViewer().getWidget3D().setRenderer(renderer);
        this.freeCamController.setCamera(renderer.camera());

        ((AbstractWorldRenderer<?>)renderer).separateRenderers.add(this::toolManagerRender);
    }

    private void toolManagerRender(ModelBatch rootBatch, float delta) {
        rootBatch.render(toolManager);
    }

    @Override
    public void act(float delta) {
        this.freeCamController.update();
        this.toolManager.act();
        super.act(delta);
    }

    protected void resize(int w, int h) {
        getViewport().update(w, h, true);
    }

    public Toaster getToaster() {
        return toaster;
    }

    public void reset() {
        clearChildren(root);

        init();
    }

    public void clearChildren(Group group) {
        if(!group.hasChildren()) return;
        group.getChildren().forEach(child -> {
            if(child instanceof Group) clearChildren((Group) child);
            if(child instanceof Disposable) ((Disposable) child).dispose();
        });
        group.clearChildren();
    }

    public void fixLayering() {
        menuBar.getTable().toFront();
    }
}
