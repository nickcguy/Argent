package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Guy on 21/07/2016.
 */
public class LabeledActor extends Table {

    private Label label;
    private Actor actor;

    private Cell<Label> labelCell;
    private Cell<Actor> actorCell;

    public LabeledActor(String label, Actor actor) {
        this(VisUI.getSkin(), label, actor);
    }

    public LabeledActor(Skin skin, String label, Actor actor) {
        super(skin);
        this.label = new Label(label, skin);
        this.actor = actor;

        this.labelCell = add(this.label).width(this.label.getWidth());
        this.actorCell = add(this.actor).width(this.actor.getWidth());
        this.row();
    }

    public Label label() { return label; }
    public Actor actor() { return actor; }

    public Cell<Label> labelCell() { return labelCell; }
    public Cell<Actor> actorCell() { return actorCell; }
}
