package net.ncguy.argent.editor.swing.shader;

import com.badlogic.gdx.Gdx;
import net.ncguy.argent.editor.swing.IEditorPane;
import net.ncguy.argent.render.renderer.DynamicRenderer;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.utils.FileUtils;
import net.ncguy.argent.world.GameWorld;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderEditor<T> implements IEditorPane<T> {

    private GameWorld.Generic<T> gameWorld;

    protected ShaderForm shaderForm;
    private static Map<String, Theme> themeMap = new HashMap<>();

    public ShaderForm shaderForm() { return shaderForm; }

    public ShaderEditor(GameWorld.Generic<T> gameWorld) {
        this.gameWorld = gameWorld;
        SwingUtilities.invokeLater(() -> {
            shaderForm = new ShaderForm(this);
            shaderForm.World(gameWorld);
            shaderForm.onApply = () -> Gdx.app.postRunnable(this::compile);
        });
    }

    public void compile() {
        Stack<DynamicShader.Info> infoStack = getShaderStack();
        if(infoStack == null) return;
        this.gameWorld.renderer().clearRenderPipe();
        while(infoStack.size() > 1)
            this.gameWorld.renderer().addBufferRenderers(new DynamicRenderer<>(this.gameWorld.renderer(), infoStack.pop()));
        this.gameWorld.renderer().setFinalBuffer(new DynamicRenderer<>(this.gameWorld.renderer(), infoStack.pop()));
    }

    @Override
    public ShaderEditor<T> init(JMenuBar menuBar) {

        JMenu shaderMenu = new JMenu("Shader");

        try {
            File themeFile = new File("themes");
            System.out.println(themeFile.getAbsolutePath());
            File[] files = themeFile.listFiles();
            for (File file : files) {
                Theme theme = Theme.load(new FileInputStream(file));
                themeMap.put(FileUtils.getFileName(file).toLowerCase(), theme);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMenu themeMenu = new JMenu("Theme");
        themeMap.forEach((s, t) -> themeMenu.add(new JMenuItem(new AbstractAction(s) {
            @Override
            public void actionPerformed(ActionEvent e) {
                t.apply(shaderForm.vertexShaderArea());
                t.apply(shaderForm.fragmentShaderArea());
            }
        })));

        shaderMenu.add(themeMenu);

        JCheckBoxMenuItem realtimeCheck = new JCheckBoxMenuItem("Realtime update");
        realtimeCheck.addActionListener(e -> shaderForm.realtime(realtimeCheck.isSelected()));
        shaderMenu.add(realtimeCheck);

        menuBar.add(shaderMenu);
        return this;
    }

    @Override
    public JComponent getRootComponent() {
        return shaderForm.$$$getRootComponent$$$();
    }

    public Stack<DynamicShader.Info> getShaderStack() {
        return shaderForm.compileToStack();
    }
}
