package net.ncguy.argent.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import net.ncguy.argent.Argent;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static com.badlogic.gdx.graphics.GL30.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL30.GL_DEPTH_BUFFER_BIT;

/**
 * Created by Guy on 15/07/2016.
 */
public class EditorScreen implements Screen {

    @Inject ProjectManager projectManager;
    @Inject EditorUI editorUI;


    @Override
    public void show() {
        Argent.loadModule(new EditorModule());

        ArgentInjector.inject(this);

        editorUI.reset();

        IntBuffer b = BufferUtils.createIntBuffer(1);
        Gdx.gl.glGetIntegerv(GL30.GL_MAX_COLOR_ATTACHMENTS, b);
        System.out.println("GL30.GL_MAX_COLOR_ATTACHMENTS: "+b.get(0));

        editorUI.setRenderer(projectManager.current().currScene.sceneGraph.renderer);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        projectManager.current().currScene.sceneGraph.update(delta);
        projectManager.current().currScene.sceneGraph.render(delta);

        GlobalSettings.exposure = 1f;
    }

    @Override
    public void resize(int width, int height) {
        editorUI.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

