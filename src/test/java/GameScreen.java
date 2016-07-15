import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.character.Character;
import net.ncguy.argent.character.WorldObjectCharacter;
import net.ncguy.argent.character.controller.PlayerController;
import net.ncguy.argent.editor.swing.EditorRootConfig;
import net.ncguy.argent.physics.BulletEntity;
import net.ncguy.argent.render.WorldRenderer;
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
    private GameWorld.Generic masterWorld;
    private GameWorld.Generic<WorldObject> gameWorld;
    private GameWorld.Generic<BulletEntity<WorldObject>> physicsWorld;
    private WorldRenderer<WorldObject> renderer;
    private Stage stage;
    private Table table;
    private ScrollPane scrollpane;
    private CameraInputController cameraController;
    private InputMultiplexer multiplexer;
    private List<BufferWidget> widgets;
    private Character character;
    private PlayerController playerController;

    public void attachPlayerController() {
        playerController = new PlayerController(this.renderer.camera(), character = WorldObjectCharacter.createDefaultPlayerCharacter(masterWorld));
//        playerController.cameraOffset().y = 160;
        playerController.attachInputProcessor();
//        this.masterWorld.addLandscape(new Vector3(0, 500, 0), 64, 64, 100, 100);
    }

    public GameScreen(TestLauncher game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));
        this.instances = new ArrayList<>();

        this.gameWorld = GameWorldFactory.worldObjectWorld(this.instances);
        this.physicsWorld = GameWorldFactory.wrapper_worldObjectPhysics(this.instances);

        this.masterWorld = this.gameWorld;
//        this.masterWorld = this.physicsWorld;

        this.renderer = this.masterWorld.renderer();

        if(Argent.useHMD()) this.cameraController = new OVRCameraController(this.renderer.camera());
        else this.cameraController = new FirstPersonCameraInputController(this.renderer.camera());

        this.table = new Table();
        this.scrollpane = new ScrollPane(this.table);
        this.stage.addActor(this.scrollpane);
        this.scrollpane.setBounds(5, 5, 256, Gdx.graphics.getHeight()-10);
        this.scrollpane.setScrollBarPositions(true, false);

        this.multiplexer = new InputMultiplexer(this.stage);
        Gdx.input.setInputProcessor(this.multiplexer);

        Argent.attachEditor(EditorRootConfig.Factory.buildConfig(this.masterWorld));
        this.masterWorld.onLoad = this::attachPlayerController;

        this.widgets = new ArrayList<>();
        this.masterWorld.consoleSkin = VisUI.getSkin();
        this.masterWorld.consoleEnabled = true;


    }

    @Override
    public void render(float delta) {
        if(playerController != null) playerController.update();
        Gdx.graphics.setTitle(this.renderer.camera().position.toString());
//        Gdx.graphics.setTitle(Gdx.graphics.getFramesPerSecond()+"");

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        this.cameraController.update();



        this.masterWorld.renderer().camera().far = 2000;
        Argent.draw(() -> this.masterWorld.render(delta));
        this.masterWorld.updateBuffers(this.stage, this.table, this.widgets, VisUI.getSkin());
        this.stage.act(delta);
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.masterWorld.renderer().resize(width, height);
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
