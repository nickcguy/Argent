package net.ncguy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.EditorModule;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.entity.EntityModule;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.LightComponent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.injector.InjectionModule;
import net.ncguy.argent.render.argent.ArgentRendererModule;

import static com.badlogic.gdx.graphics.GL30.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL30.GL_DEPTH_BUFFER_BIT;

/**
 * Created by Guy on 15/07/2016.
 */
public class GameScreen implements Screen {


    @Inject ProjectManager projectManager;
    @Inject EditorUI editorUI;

    @Override
    public void show() {
//        UIModule.handle(Gdx.files.internal("ui/uiskin.json"));
        Argent.loadModule(new EditorModule());
        Argent.loadModule(new ArgentRendererModule());
        Argent.loadModule(new EntityModule());
        Argent.loadModule(new InjectionModule());

        ArgentInjector.inject(this);

//        renderer = new ArgentRenderer<>(world);

        Material mtl = new Material();
        mtl.set(TextureAttribute.createDiffuse(Argent.content.get("Texture_testDiffuse", Texture.class)));
        mtl.set(TextureAttribute.createNormal(Argent.content.get("Texture_testNormal", Texture.class)));
        mtl.set(TextureAttribute.createSpecular(Argent.content.get("Texture_testSpecular", Texture.class)));

        WorldEntity worldEntity = new WorldEntity();
        worldEntity.add(new LightComponent(worldEntity));

        projectManager.current().currScene.sceneGraph.addWorldEntity(worldEntity);

        editorUI.setRenderer(projectManager.current().currScene.sceneGraph.renderer);

        projectManager.current().currScene.select(worldEntity);

        Gdx.input.setInputProcessor(new InputMultiplexer(editorUI, editorUI.getFreeCamController()));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        projectManager.current().currScene.sceneGraph.update(delta);
        projectManager.current().currScene.sceneGraph.render(delta);


//        editorUI.act(delta);
//        editorUI.draw();

//        renderer.render(delta);

//        stage.act(delta);
//        stage.draw();

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

