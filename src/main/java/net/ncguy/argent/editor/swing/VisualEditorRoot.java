package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.bulenkov.darcula.DarculaLaf;
import net.ncguy.argent.Argent;
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
public class VisualEditorRoot<T> implements Disposable {

    private VisualEditorRootConfig<T> config;

    private GameWorld.Generic<T> gameWorld;
    private ShaderEditor<T> shaderEditor;

    protected int keyCode = Input.Keys.P;
    protected int requiredModifier = Input.Keys.SHIFT_LEFT;


    public VisualEditorRoot(VisualEditorRootConfig<T> config) {
        this.config = config;
        this.gameWorld = config.gameWorld;
        this.shaderEditor = new ShaderEditor<>(config.gameWorld);

        installLookAndFeel("Darcula", DarculaLaf.class);
    }

    public void addToStage(Stage stage) {
        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == keyCode) {
                    if(Gdx.input.isKeyPressed(requiredModifier) || requiredModifier <= -1)
                        SwingUtilities.invokeLater(() -> openEditor());
                }
                return super.keyDown(event, keycode);
            }
        });
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
        frame.dispose();
        frame = null;
        gameWorld.dispose();
        gameWorld = null;
    }
}
