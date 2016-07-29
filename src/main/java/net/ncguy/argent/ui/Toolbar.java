package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;

/**
 * Created by Guy on 27/07/2016.
 */
public class Toolbar {

    protected Table root;
    protected Table left, right;

    public Toolbar() {
        root = new Table(VisUI.getSkin());
        root.setBackground("menu-bg");
        root.align(Align.left | Align.center);

        left = new Table(VisUI.getSkin());
        left.left().top();
        root.add(left).pad(2).expandX().fillX();

        right = new Table(VisUI.getSkin());
        right.right().top();
        root.add(right).pad(2).expandX().fillX().row();
        root.add(new Separator()).fillX().expandX().height(2).colspan(2);
    }

    public void addItem(Actor actor, boolean addLeft) {
        if(addLeft) left.add(actor);
        else right.add(actor);
    }

    public void addSeparator(boolean addLeft) {
        if(addLeft) left.add(new Separator()).fillY().expandY();
        else right.add(new Separator()).fillY().expandY();
    }

    public Table getRoot() { return root; }
}
