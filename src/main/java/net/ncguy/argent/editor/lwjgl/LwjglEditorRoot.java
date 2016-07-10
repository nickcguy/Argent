package net.ncguy.argent.editor.lwjgl;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.editor.lwjgl.app.LwjglEditorListener;
import net.ncguy.argent.editor.swing.EditorRootConfig;
import net.ncguy.argent.view.ArgentWindowFactory;

/**
 * Created by Guy on 04/07/2016.
 */
public class LwjglEditorRoot<T> implements Disposable {

    private final EditorRootConfig<T> editorCfg;
    private Lwjgl3Window editorWindow;

    public LwjglEditorRoot(EditorRootConfig<T> editorCfg) {
        this.editorCfg = editorCfg;
    }

    public void spawnWindow() {
        editorWindow = ArgentWindowFactory.instance().window(new LwjglEditorListener());
    }

    @Override
    public void dispose() {

    }
}
