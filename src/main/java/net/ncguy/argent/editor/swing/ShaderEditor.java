package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import net.ncguy.argent.render.renderer.DynamicRenderer;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.world.GameWorld;

import javax.swing.*;
import java.util.Stack;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderEditor<T> {

    private boolean editorOpen = false;
    private GameWorld.Generic<T> gameWorld;

    protected int keyCode = Input.Keys.P;
    protected int requiredModifier = Input.Keys.SHIFT_LEFT;

    protected ShaderForm shaderForm;

    public ShaderEditor(GameWorld.Generic<T> gameWorld) {
        this.gameWorld = gameWorld;
        SwingUtilities.invokeLater(() -> {
            shaderForm = ShaderForm.getForm();
            shaderForm.World(gameWorld);
            shaderForm.onApply = () -> Gdx.app.postRunnable(this::compile);
        });
    }

    public void openEditor() {
        update(true);
    }

    public void compile() {
        Stack<DynamicShader.Info> infoStack = shaderForm.compileToStack();
        if(infoStack == null) return;
        this.gameWorld.renderer().clearRenderPipe();
        while(infoStack.size() > 1)
            this.gameWorld.renderer().addBufferRenderers(new DynamicRenderer<>(this.gameWorld.renderer(), infoStack.pop()));
        this.gameWorld.renderer().setFinalBuffer(new DynamicRenderer<>(this.gameWorld.renderer(), infoStack.pop()));
    }

    public void closeEditor() {
        update(false);
    }

    private void update() {
        frame().setVisible(editorOpen);
    }

    private void update(boolean newState) {
        editorOpen = newState;
        update();
    }

    public void addToStage(Stage stage) {
        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == keyCode) {
                    if(Gdx.input.isKeyPressed(requiredModifier) || requiredModifier <= -1) {
                        openEditor();
                    }
                }
                return super.keyDown(event, keycode);
            }
        });
    }

    private static JFrame frame() {
        return ShaderForm.getFrame();
    }

}
