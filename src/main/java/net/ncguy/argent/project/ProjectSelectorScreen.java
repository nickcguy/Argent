package net.ncguy.argent.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import net.ncguy.argent.Argent;
import net.ncguy.argent.ArgentGame;
import net.ncguy.argent.editor.EditorScreen;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.project.ContextSelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.project.widget.ProjectDetails;
import net.ncguy.argent.project.widget.ProjectSelector;
import net.ncguy.argent.project.widget.selector.ProjectSelectorControls;
import net.ncguy.argent.utils.InputManager;

import java.util.List;

/**
 * Created by Guy on 25/08/2016.
 */
public class ProjectSelectorScreen implements Screen, ContextSelectedEvent.ContextSelectedListener {

    Stage stage;
    Table root;
    ProjectSelector selector;
    ProjectSelectorControls controls;
    ProjectDetails details;


    @Inject
    InputManager inputManager;
    @Inject
    ProjectManager projectManager;

    @Inject
    ArgentGame game;

    @Override
    public void show() {
        ArgentInjector.inject(this);
        Argent.event.register(this);
        stage = new Stage(new ScreenViewport(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));

        selector = new ProjectSelector();
        controls = new ProjectSelectorControls();
        details = new ProjectDetails(projectManager);
        root = new Table(VisUI.getSkin());
        root.setFillParent(true);

        List<ProjectContext> projects = projectManager.getContexts();
        projects.forEach(selector::addProject);

        root.add(selector).width(512).growY().top().left();

        VisSplitPane splitPane = new VisSplitPane(controls, details, true);
        splitPane.setSplitAmount(.4f);
        splitPane.setMinSplitAmount(.1f);
        splitPane.setMaxSplitAmount(.8f);

        root.add(splitPane).grow().row();

        stage.addActor(root);

        inputManager.clear();
        inputManager.addProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        controls.toFront();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;
        stage.getCamera().update(true);
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

    @Override
    public void onContextSelected(ContextSelectedEvent event) {
        Gdx.app.postRunnable(() -> {
            game.setScreen(new EditorScreen());
        });
    }
}
