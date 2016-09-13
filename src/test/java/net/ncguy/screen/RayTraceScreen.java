package net.ncguy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import net.ncguy.argent.render.experimental.ray.RayTraceRenderer;

/**
 * Created by Guy on 11/09/2016.
 */
public class RayTraceScreen implements Screen {

    OrthographicCamera screenCamera;
    PerspectiveCamera worldCamera;
    RayTraceRenderer renderer;

    @Override
    public void show() {
        worldCamera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.position.set(3, 2, 7);
        worldCamera.up.set(0, 1, 0);
        worldCamera.lookAt(0, .5f, 0);
        worldCamera.near = 0.1f;
        worldCamera.far = 1000;
        renderer = new RayTraceRenderer(worldCamera, screenCamera);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderer.render(null);

        worldCamera.update(true);
        screenCamera.update(true);
    }

    @Override
    public void resize(int width, int height) {

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
