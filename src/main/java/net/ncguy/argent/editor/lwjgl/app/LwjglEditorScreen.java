package net.ncguy.argent.editor.lwjgl.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.ncguy.argent.editor.lwjgl.app.panel.RootPanel;
import net.ncguy.argent.pipe.ObjectPipe;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 04/07/2016.
 */
public class LwjglEditorScreen implements Screen {

    private Vector2 screenSize = AppUtils.Graphics.getPackedSize();
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private Stage stage;

    private RootPanel rootPanel;

    @Override
    public void show() {
        camera = new OrthographicCamera(screenSize.x, screenSize.y);
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        rootPanel = new RootPanel(stage, ObjectPipe.get("active.gameworld.generic", GameWorld.Generic.class));

        stage.addActor(rootPanel);
        Gdx.input.setInputProcessor(stage);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1f);

        stage.setDebugAll(false);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.setToOrtho(false, width, height);
        camera.update(true);
        rootPanel.setBounds(0, 0, width, height);
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
