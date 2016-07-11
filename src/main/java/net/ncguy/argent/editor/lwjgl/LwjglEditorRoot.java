package net.ncguy.argent.editor.lwjgl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.editor.IEditorRoot;
import net.ncguy.argent.editor.lwjgl.app.LwjglEditorListener;
import net.ncguy.argent.editor.swing.EditorRootConfig;
import net.ncguy.argent.view.ArgentWindowFactory;

/**
 * Created by Guy on 04/07/2016.
 */
public class LwjglEditorRoot<T> implements IEditorRoot, Disposable {

    private final EditorRootConfig<T> editorCfg;
    private Lwjgl3Window editorWindow;

    protected InputListener keyPressListener;
    protected int keyCode = Input.Keys.P;
    protected int requiredModifier = Input.Keys.SHIFT_LEFT;

    public LwjglEditorRoot(EditorRootConfig<T> editorCfg) {
        this.editorCfg = editorCfg;
        keyPressListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == keyCode) {
                    if(Gdx.input.isKeyPressed(requiredModifier) || requiredModifier <= -1)
                        spawnWindow();
                }
                return super.keyDown(event, keycode);
            }
        };
    }

    public void spawnWindow() {
        if(editorWindow == null)
            editorWindow = ArgentWindowFactory.instance().window(new LwjglEditorListener());
    }

    @Override
    public void dispose() {
        editorWindow.dispose();
    }

    @Override
    public void addToStage(Stage stage) {
        stage.addListener(keyPressListener);
    }

    @Override
    public void removeFromStage(Stage stage) {
        stage.removeListener(keyPressListener);
    }
}
