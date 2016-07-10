import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.lwjgl.LwjglEditorRoot;
import net.ncguy.argent.editor.swing.VisualEditorRoot;
import net.ncguy.argent.editor.swing.EditorRootConfig;
import net.ncguy.argent.pipe.ObjectPipe;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.sample.*;
import net.ncguy.argent.ui.BufferWidget;
import net.ncguy.argent.utils.FirstPersonCameraInputController;
import net.ncguy.argent.utils.Reference;
import net.ncguy.argent.vr.OVRCameraController;
import net.ncguy.argent.world.GameWorld;
import net.ncguy.argent.world.GameWorldFactory;
import net.ncguy.argent.world.WorldObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 22/06/2016.
 */
public class GameScreen implements Screen {

    TestLauncher game;
    private VisualEditorRoot<WorldObject> editor;
    private LwjglEditorRoot<WorldObject> glEditor;
    private List<WorldObject> instances;
    private GameWorld.Generic<WorldObject> gameWorld;
    private WorldRenderer<WorldObject> renderer;
    private Stage stage;
    private Table table;
    private ScrollPane scrollpane;
    private CameraInputController cameraController;
    private InputMultiplexer multiplexer;
    private List<BufferWidget> widgets;

    public GameScreen(TestLauncher game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));
        this.instances = new ArrayList<>();

//        Model cornell = Argent.content.get("Model_Cornell");
        ModelInstance inst = new ModelInstance(new Model());

        inst.materials.clear();
        Material[] mtl = new Material[] {Reference.Defaults.Models.material().copy()};
        ((TextureAttribute)mtl[0].get(TextureAttribute.Diffuse)).textureDescription.texture = Argent.content.get("Texture_Brick_DC", Texture.class);
        ((TextureAttribute)mtl[0].get(TextureAttribute.Normal)).textureDescription.texture = Argent.content.get("Texture_Brick_N", Texture.class);

        mtl[0].set(TextureAttribute.createEmissive(Argent.content.get("Texture_Brick_E", Texture.class)));
//        inst.materials.add(mtl[0]);

        inst.model.materials.clear();
        inst.model.materials.add(mtl[0]);
        inst.nodes.forEach(node -> node.parts.forEach(part -> part.material = mtl[0]));

        this.gameWorld = GameWorldFactory.worldObjectWorld(this.instances);
        ObjectPipe.register("active.gameworld.generic", () -> this.gameWorld);
//        this.gameWorld = GameWorldFactory.worldObjectPhysics(this.instances);


//        Model mercy = Argent.content.get("Model_Mercy");
        ModelInstance mercyInst = new ModelInstance(new Model());
        mercyInst.materials.clear();
        mercyInst.materials.add(mtl[0]);
        mercyInst.model.materials.clear();
        mercyInst.model.materials.add(mtl[0]);
        mercyInst.nodes.forEach(node -> node.parts.forEach(part -> part.material = mtl[0]));

        ModelInstance sphereInst = new ModelInstance(new ModelBuilder().createSphere(100, 100, 100, 64, 64, mtl[0], Reference.Defaults.Models.defaultAttributes));

        this.gameWorld.addInstance(new WorldObject(gameWorld, inst, "Model_Cornell"));
        this.gameWorld.addInstance(new WorldObject(gameWorld, mercyInst, "Model_Mercy"));

        this.renderer = this.gameWorld.renderer();

        this.renderer.addBufferRenderers(new DepthRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new DiffuseRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new UberRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new NormalRenderer<>(this.renderer));
        this.renderer.setFinalBuffer(new SceneRenderer<>(this.renderer));

        EditorRootConfig<WorldObject> editorConfig = new EditorRootConfig<>();
        editorConfig.gameWorld = this.gameWorld;
        this.editor = new VisualEditorRoot<>(editorConfig);
        this.glEditor = new LwjglEditorRoot<>(editorConfig);

        if(Argent.useHMD()) this.cameraController = new OVRCameraController(this.renderer.camera());
        else this.cameraController = new FirstPersonCameraInputController(this.renderer.camera());

        this.table = new Table();
        this.scrollpane = new ScrollPane(this.table);
        this.stage.addActor(this.scrollpane);
        this.scrollpane.setBounds(5, 5, 256, Gdx.graphics.getHeight()-10);
        this.scrollpane.setScrollBarPositions(true, false);

        this.multiplexer = new InputMultiplexer(this.stage, this.cameraController);
        Gdx.input.setInputProcessor(this.multiplexer);

        this.widgets = new ArrayList<>();
        this.gameWorld.consoleSkin = VisUI.getSkin();
        this.gameWorld.consoleEnabled = true;
    }

    @Override
    public void render(float delta) {
        this.gameWorld.renderer().camera().far = 2000;
        Gdx.graphics.setTitle(this.gameWorld.renderer().camera().position.toString());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(Gdx.input.isKeyJustPressed(Input.Keys.HOME)) {
            this.editor.addToStage(stage);
            this.glEditor.spawnWindow();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.END))
            this.editor.removeFromStage(stage);

        this.cameraController.update();
        Argent.draw(() -> this.gameWorld.render(delta));
        this.gameWorld.updateBuffers(this.stage, this.table, this.widgets, VisUI.getSkin());
        this.stage.act(delta);
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.gameWorld.renderer().resize(width, height);
        this.stage.getViewport().update(width, height, true);
        this.scrollpane.setBounds(5, 5, 256, Gdx.graphics.getHeight()-10);
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
