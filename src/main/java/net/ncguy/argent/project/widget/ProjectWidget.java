package net.ncguy.argent.project.widget;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.event.project.MetaSelectedEvent;
import net.ncguy.argent.project.ProjectMeta;
import net.ncguy.argent.project.widget.selector.ProjectWidgetContextMenu;
import net.ncguy.argent.tween.accessor.ActorTweenAccessor;
import net.ncguy.argent.tween.accessor.ColorTweenAccessor;
import net.ncguy.argent.vpl.VPLReference;

/**
 * Created by Guy on 25/08/2016.
 */
public class ProjectWidget extends Table {

    ProjectMeta meta;
    Group underlineContainer;
    Image underline;
    float tweenDuration = .3f;
    Image overlay;
    ProjectWidgetContextMenu menu;

    Label name;
    Label size;
    Label path;
    Label created;
    Label accessed;

    public ProjectWidget(ProjectMeta meta) {
        super(VisUI.getSkin());
        this.meta = meta;
        initUI();
        attachListeners();
    }

    private void initUI() {
        setBackground("menu-bg");

        name = new Label("", VisUI.getSkin());
        add(name).left().expandX().fillX();
        size = new Label("", VisUI.getSkin(), "small");
        add(size).right().row();

        path = new Label("", VisUI.getSkin(), "small");
        add(path).left().row();

        created = new Label("Created: " + this.meta.created, VisUI.getSkin(), "small");
        add(created).right().colspan(2).row();
        accessed = new Label("Last accessed: " + this.meta.lastAccessed, VisUI.getSkin(), "small");
        add(accessed).right().colspan(2).row();

        underlineContainer = new Group();
        underline = new Image(VisUI.getSkin(), "VisBlue");
        underlineContainer.addActor(underline);
        float height = 2;
        add(underlineContainer).expandX().fillX().bottom().height(height).colspan(2).row();
        underline.setWidth(0);
        underline.setX(-3);
        underline.setHeight(height);

        overlay = new Image(VisUI.getSkin(), "SolidWhite");
        overlay.getColor().a = 0;
        overlay.setFillParent(true);

        addActor(overlay);
        menu = new ProjectWidgetContextMenu(meta);

        getChildren().forEach(child -> {
            child.setTouchable(Touchable.disabled);
        });
        setTouchable(Touchable.enabled);

        update();
    }

    private void attachListeners() {
        attachHoverListener();
        attachClickListeners();
    }

    private void attachHoverListener() {
        addListener(new InputListener() {
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { onEnter(); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { onExit(); }
        });
    }

    private void attachClickListeners() {
        addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectThis();
            }
        });
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                openMenu(pos.x, pos.y);
            }
        });
    }

    public void onEnter() {
        tweenDuration = .25f;
        Tween.to(overlay.getColor(), ColorTweenAccessor.ALPHA, tweenDuration).target(.05f).start(Argent.tween);
        Tween.to(underline, ActorTweenAccessor.X | ActorTweenAccessor.W, tweenDuration).target(0, underlineContainer.getWidth()).start(Argent.tween);
    }
    public void onExit() {
        Tween.to(overlay.getColor(), ColorTweenAccessor.ALPHA, tweenDuration).target(0).start(Argent.tween);
        Tween.to(underline, ActorTweenAccessor.X | ActorTweenAccessor.W, tweenDuration).target(-3, 0).start(Argent.tween);
    }

    public void selectThis() {
        new MetaSelectedEvent(this.meta).fire();
    }

    public void openMenu(float x, float y) {
        menu.showMenu(getStage(), x, y);
    }

    public void update() {
        this.name.setText(this.meta.name);
        this.path.setText(this.meta.path);
        this.meta.calculateSize();
        this.size.setText(this.meta.size());
        if(this.meta.rawSize() < 0) setColor(VPLReference.PinColours.RED);
        else setColor(Color.WHITE);
    }
}
