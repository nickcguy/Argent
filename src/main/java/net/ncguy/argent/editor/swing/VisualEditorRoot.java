package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.bulenkov.darcula.DarculaLaf;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.Argent;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.editor.IEditorRoot;
import net.ncguy.argent.editor.swing.object.ObjectEditor;
import net.ncguy.argent.editor.swing.shader.ShaderEditor;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.world.GameWorld;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.event.ActionEvent;
import java.util.Stack;

/**
 * Created by Guy on 27/06/2016.
 */
public class VisualEditorRoot<T> implements IEditorRoot, Disposable {

    private EditorRootConfig<T> config;

    private GameWorld.Generic<T> gameWorld;
    public ShaderEditor<T> shaderEditor;
    private ObjectEditor<T> objectEditor;

    protected int keyCode = Input.Keys.O;
    protected int requiredModifier = Input.Keys.SHIFT_LEFT;

    protected VarRunnables.Var2Runnable<Integer> onResize;
    protected InputListener keyPressListener;

    protected Group uiGroup;
    // TODO replace with own skinnable menubar
    MenuBar menuBar;

    public static VisualEditorRoot recentEditor;

    public VisualEditorRoot(EditorRootConfig<T> config) {
        this.config = config;
        this.gameWorld = config.gameWorld;
        this.shaderEditor = new ShaderEditor<>(config.gameWorld);
        this.objectEditor = new ObjectEditor<>(config.gameWorld);

        onResize = this::resize;
        keyPressListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == keyCode) {
                    if(Gdx.input.isKeyPressed(requiredModifier) || requiredModifier <= -1)
                        SwingUtilities.invokeLater(() -> openEditor());
                }
                return super.keyDown(event, keycode);
            }
        };
        installLookAndFeel("Darcula", DarculaLaf.class);
        recentEditor = this;
    }

    public Group uiGroup() {
        if(uiGroup == null) initUI();
        return uiGroup;
    }

    private void initUI () {
        uiGroup = new Group();
        initMenuBar();
    }
    private void initMenuBar() {
        menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem editorItem = new MenuItem("Open Editor", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SwingUtilities.invokeLater(() -> openEditor());
            }
        });
        editorItem.setShortcut(requiredModifier, keyCode);
        fileMenu.addItem(editorItem);

        menuBar.addMenu(fileMenu);

        uiGroup().addActor(menuBar.getTable());
    }

    private void startTween() { startTween(.3f); }
    private void startTween(float duration) {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Hiddens
        tweenToHidden(0);

        // Targets
        menuBar.getTable().addAction(Actions.parallel(Actions.moveTo(0, Gdx.graphics.getHeight()-menuBar.getTable().getHeight(), duration), Actions.fadeIn(duration)));
    }
    private void tweenToHidden(float duration) { tweenToHidden(duration, null); }
    private void tweenToHidden(float duration, Runnable callback) {
        Table menuTable = menuBar.getTable();

        /*
        menuBar.getTable().setPosition(0, Gdx.graphics.getHeight()+menuBar.getTable().getHeight());
        menuBar.getTable().getColor().a = 0;
         */

        menuTable.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(0, Gdx.graphics.getHeight()+menuBar.getTable().getHeight(), duration),
                Actions.fadeOut(duration)
            ), Actions.run(() ->{
                if(callback != null) callback.run();
            })
        ));
    }

    public void addToStage(Stage stage) {
        stage.addListener(keyPressListener);
        stage.addActor(uiGroup());
        Argent.onResize.add(onResize);
        startTween();
//        onResize.run(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void removeFromStage(Stage stage) {
        stage.removeListener(keyPressListener);
        tweenToHidden(.3f, () -> uiGroup().remove());
    }

    public void resize(int width, int height) {
        uiGroup().setDebug(true, true);
        uiGroup().setBounds(0, 0, width, height);
        menuBar.getTable().setSize(width, 25);
        menuBar.getTable().setPosition(0, height-menuBar.getTable().getHeight());
    }

    private void openEditor() {
        if(frame == null) {
            this.init();
        }
        if(frame == null) {
            Argent.toast("Editor", "Unable to open, frame is null", null);
            return;
        }
        frame.setVisible(true);
    }

    // Form

    private JFrame frame;
    private RootForm form;

    private void init() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Get shader order") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gdx.app.postRunnable(() -> {
                    Stack<DynamicShader.Info> stack = shaderEditor.getShaderStack();
                    System.out.println("Shader order");
                    while(!stack.isEmpty()) {
                        System.out.printf("\t%s\n", stack.pop().toString());
                    }
                });
            }
        });
        menuBar.add(fileMenu);

        JMenu lafMenu = new JMenu("Look and Feel");
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            lafMenu.add(new AbstractAction(info.getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLookAndFeel(info.getClassName());
                }
            });
        }
        menuBar.add(lafMenu);

        form = new RootForm();
        form.rootTabControl().addTab("Objects", objectEditor.init(menuBar).getRootComponent());
        form.rootTabControl().addTab("Shaders", shaderEditor.init(menuBar).getRootComponent());

        frame = new JFrame("Editor");
        frame.setContentPane(form.$$$getRootComponent$$$());

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(626, 446);
        frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);

        frame.setJMenuBar(menuBar);
    }

    public void updateLookAndFeel(Class<? extends BasicLookAndFeel> cls) {
        updateLookAndFeel(cls.getCanonicalName());
    }
    public void updateLookAndFeel(String canonicalName) {
        SwingUtilities.invokeLater(() -> {
            try {
                if(canonicalName.equalsIgnoreCase(MetalLookAndFeel.class.getCanonicalName()))
                    MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                UIManager.setLookAndFeel(canonicalName);
                SwingUtilities.updateComponentTreeUI(frame);
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void installLookAndFeel(Class<? extends BasicLookAndFeel> cls) {
        installLookAndFeel(cls.getSimpleName(), cls.getCanonicalName());
    }
    public void installLookAndFeel(String name, Class<? extends BasicLookAndFeel> cls) {
        installLookAndFeel(name, cls.getCanonicalName());
    }
    public void installLookAndFeel(String name, String canonicalName) {
        SwingUtilities.invokeLater(() -> UIManager.installLookAndFeel(name, canonicalName));
    }

    @Override
    public void dispose() {
        Argent.onResize.remove(onResize);

        frame.dispose();
        frame = null;
        gameWorld.dispose();
        gameWorld = null;
    }
}
