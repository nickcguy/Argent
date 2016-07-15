package net.ncguy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import net.ncguy.ArgentSample;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 15/07/2016.
 */
public class LoaderScreen implements Screen {

    ArgentSample game;

    public LoaderScreen(ArgentSample game) {
        this.game = game;
    }

    @Override
    public void show() {
        Argent.content.setOnFinish((manager) -> {
            System.out.println("Loaded assets");
            Argent.content.assetMap().forEach((k, v) -> System.out.printf("\t%s: %s", k, v));
            this.game.setScreen(new GameScreen());
        });
        Argent.content.addDirectoryRoot(Gdx.files.internal("assets"), Texture.class, "png");
        Argent.content.start();
    }

    @Override
    public void render(float delta) {
        Argent.content.update();
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
