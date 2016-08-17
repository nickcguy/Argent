package net.ncguy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
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
import net.ncguy.argent.entity.components.model.primitive.PrimitiveCubeModelComponent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.injector.InjectionModule;
import net.ncguy.argent.render.argent.ArgentRendererModule;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

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

        IntBuffer b = BufferUtils.createIntBuffer(1);
        Gdx.gl.glGetIntegerv(GL30.GL_MAX_COLOR_ATTACHMENTS, b);
        System.out.println("GL30.GL_MAX_COLOR_ATTACHMENTS: "+b.get(0));

//        renderer = new ArgentRenderer<>(world);

        Material mtl = new Material();
        mtl.id = "Brick2";
        mtl.set(TextureAttribute.createDiffuse(Argent.content.get("Texture_brick_dc", Texture.class)));
        mtl.set(TextureAttribute.createNormal(Argent.content.get("Texture_brick_n", Texture.class)));
        mtl.set(TextureAttribute.createSpecular(Argent.content.get("Texture_brick_S", Texture.class)));
        mtl.set(TextureAttribute.createAmbient(Argent.content.get("Texture_brick_ao", Texture.class)));
        mtl.set(TextureAttribute.createBump(Argent.content.get("Texture_Brick_d", Texture.class)));
        mtl.set(TextureAttribute.createEmissive(Argent.content.get("Texture_Brick_E", Texture.class)));

        Material mtl2 = new Material();
        mtl2.id = "Test";
        mtl2.set(TextureAttribute.createDiffuse(Argent.content.get("testDiffuse", Texture.class)));
        mtl2.set(TextureAttribute.createNormal(Argent.content.get("testNormal", Texture.class)));
        mtl2.set(TextureAttribute.createSpecular(Argent.content.get("testSpecular", Texture.class)));
        mtl2.set(TextureAttribute.createAmbient(Argent.content.get("testAO", Texture.class)));
        mtl2.set(TextureAttribute.createBump(Argent.content.get("testDisplacement", Texture.class)));
//        mtl2.set(TextureAttribute.createEmissive(Argent.content.get("Texture_Brick_E", Texture.class)));

//        ArgMaterial argMaterial = new ArgMaterial(mtl, true);
//        projectManager.current().addMaterial(argMaterial);
//        projectManager.current().addMaterial(new ArgMaterial(mtl2));

        WorldEntity worldEntity = new WorldEntity();
        PrimitiveCubeModelComponent cube = new PrimitiveCubeModelComponent(worldEntity);
        worldEntity.add(new LightComponent(worldEntity));
        worldEntity.add(cube);

        projectManager.current().currScene.sceneGraph.addWorldEntity(worldEntity);

        editorUI.setRenderer(projectManager.current().currScene.sceneGraph.renderer);

        projectManager.current().currScene.select(worldEntity);
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

