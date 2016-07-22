package net.ncguy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.ArgentSample;
import net.ncguy.argent.Argent;
import net.ncguy.argent.utils.FileUtils;

import java.util.List;

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
            Argent.content.assetMap().forEach((k, v) -> System.out.printf("\t%s: %s\n", k, v));
            this.game.setScreen(new GameScreen());
        });
        List<FileHandle> handles = FileUtils.getAllHandlesInDirectory(Gdx.files.internal("assets"));
        System.out.println("All file handles");
        handles.stream().filter(h -> !h.isDirectory()).forEach(h -> System.out.println("\t"+h.path()));
        Argent.content.addDirectoryRoot("assets", Texture.class, "png", "jpg");
        Argent.content.addDirectoryRoot("assets", Model.class, "g3db");
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
