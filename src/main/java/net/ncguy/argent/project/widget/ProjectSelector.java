package net.ncguy.argent.project.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.event.project.NewProjectEvent;
import net.ncguy.argent.event.project.RemoveProjectEvent;
import net.ncguy.argent.event.project.UpdateProjectEvent;
import net.ncguy.argent.project.ProjectMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 25/08/2016.
 */
public class ProjectSelector extends Table implements NewProjectEvent.NewProjectListener, RemoveProjectEvent.RemoveProjectListener, UpdateProjectEvent.RenameProjectListener {

    private Table content;
    private ScrollPane scroller;
    private Map<ProjectMeta, ProjectWidget> actorMap;

    private float tweenDuration = .25f;

    public ProjectSelector() {
        super(VisUI.getSkin());
        Argent.event.register(this);
        content = new Table(VisUI.getSkin());
        scroller = new ScrollPane(content);
        setBackground("default-pane");
        actorMap = new HashMap<>();

        add(scroller).grow();

        attachListeners();

        setTouchable(Touchable.enabled);
    }

    private void attachListeners() {
//        addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if(event.isStopped()) return;
//                new MetaSelectedEvent(null).fire();
//            }
//        });
    }

    public void addProject(ProjectContext context) {
        addProject(context.getMeta());
    }
    public void addProject(ProjectMeta meta) {
        ProjectWidget widget = new ProjectWidget(meta);
        actorMap.put(meta, widget);
        content.add(widget).growX().top().left().padBottom(4).row();
        widget.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.1f),
                Actions.parallel(
                        Actions.moveBy(-getWidth(), 0)
                ),
                Actions.parallel(
                        Actions.moveBy(getWidth(), 0, tweenDuration),
                        Actions.fadeIn(tweenDuration)
                )
        ));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        content.pack();
        scroller.setY(getHeight() - content.getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void onNewProject(NewProjectEvent event) {
        addProject(event.context.getMeta());
    }

    @Override
    public void onRemoveProject(RemoveProjectEvent event) {
        ProjectContext context = event.context;
        if(context == null) return;
        ProjectMeta meta = context.getMeta();
        if(meta == null) return;
        ProjectWidget actor = getActorFromMap(meta);
        if(actor == null) return;
        removeActorFromTable(actor);
    }

    public void removeActorFromTable(Actor actor) {
        removeActorFromTable(actor, tweenDuration);
    }

    public void removeActorFromTable(Actor actor, float duration) {
        actor.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(-getWidth() * 1.5f, 0, duration),
                        Actions.fadeOut(duration)
                ),
                Actions.run(() -> content.removeActor(actor))
        ));
    }

    public ProjectWidget getActorFromMap(ProjectMeta meta) {
        for (ProjectMeta key : actorMap.keySet())
            if(key.equals(meta)) return actorMap.get(key);
        return null;
//        return actorMap.get(meta);
    }

    @Override
    public void onRenameProject(UpdateProjectEvent event) {
        ProjectWidget widget = getActorFromMap(event.meta);
        widget.update();
    }
}
