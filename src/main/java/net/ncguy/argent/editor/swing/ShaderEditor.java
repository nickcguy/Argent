package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import net.ncguy.argent.world.GameWorld;

import javax.swing.*;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderEditor {

    private boolean editorOpen = false;
    private GameWorld.Generic<?> gameWorld;

    protected int keyCode = Input.Keys.P;
    protected int requiredModifier = Input.Keys.SHIFT_LEFT;

    public ShaderEditor(GameWorld.Generic<?> gameWorld) {
        this.gameWorld = gameWorld;
        SwingUtilities.invokeLater(ShaderForm::getFrame);
    }

    public void openEditor() {
        update(true);
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
