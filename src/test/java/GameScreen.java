import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.swing.EditorRootConfig;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.sample.*;
import net.ncguy.argent.ui.BufferWidget;
import net.ncguy.argent.utils.FirstPersonCameraInputController;
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

        this.gameWorld = GameWorldFactory.worldObjectWorld(this.instances);
//        this.gameWorld = GameWorldFactory.worldObjectPhysics(this.instances);


//        Model mercy = Argent.content.get("Model_Mercy");
        ModelInstance mercyInst = new ModelInstance(new Model());

        this.gameWorld.addInstance(new WorldObject(gameWorld, inst, "Model_Cornell"));
        this.gameWorld.addInstance(new WorldObject(gameWorld, mercyInst, "Model_Mercy"));

        this.renderer = this.gameWorld.renderer();

        this.renderer.addBufferRenderers(new DepthRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new DiffuseRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new UberRenderer<>(this.renderer));
        this.renderer.addBufferRenderers(new NormalRenderer<>(this.renderer));
        this.renderer.setFinalBuffer(new SceneRenderer<>(this.renderer));

        if(Argent.useHMD()) this.cameraController = new OVRCameraController(this.renderer.camera());
        else this.cameraController = new FirstPersonCameraInputController(this.renderer.camera());

        this.table = new Table();
        this.scrollpane = new ScrollPane(this.table);
        this.stage.addActor(this.scrollpane);
        this.scrollpane.setBounds(5, 5, 256, Gdx.graphics.getHeight()-10);
        this.scrollpane.setScrollBarPositions(true, false);

        this.multiplexer = new InputMultiplexer(this.stage, this.cameraController);
        Gdx.input.setInputProcessor(this.multiplexer);

        Argent.attachEditor(EditorRootConfig.Factory.buildConfig(this.gameWorld));

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
