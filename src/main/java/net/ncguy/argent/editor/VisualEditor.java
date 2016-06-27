package net.ncguy.argent.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import net.ncguy.argent.editor.swing.shader.ShaderEditor;
import net.ncguy.argent.world.GameWorld;
import net.ncguy.argent.world.GameWorldFactory;
import net.ncguy.argent.world.WorldObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 21/06/2016.
 */
public class VisualEditor implements Screen {

    private Game game;
    private Screen previousScreen;
    private ShaderEditor editor;
    private List<WorldObject> instances;
    private GameWorld.Generic<WorldObject> gameWorld;

    public VisualEditor(Game game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        this.instances = new ArrayList<>();
        this.gameWorld = GameWorldFactory.worldObjectWorld(this.instances);
        this.editor = new ShaderEditor(this.gameWorld);
    }

    public void revert() {
        this.game.setScreen(previousScreen);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
//                editor.openEditor();
            }
        }
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
