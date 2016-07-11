package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by Guy on 11/07/2016.
 */
public class LabeledActor extends Table {

    private Label label;
    private Actor actor;

    private Cell<Label> labelCell;
    private Cell<Actor> actorCell;

    public LabeledActor(Skin skin, String text, Actor actor) {
        super(skin);
        this.label = new Label(text, skin);
        this.actor = actor;

        this.labelCell = add(this.label);
        this.actorCell = add(this.actor);
        row();
    }

    public Label label() { return label; }
    public Actor actor() { return actor; }
    public Cell<Label> labelCell() { return labelCell;}
    public Cell<Actor> actorCell() { return actorCell; }
}
