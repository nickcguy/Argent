package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class ArgentTable extends Group {

    List<Cell<Actor>> children = new ArrayList<>();
    protected float cellWidth = 200;
    protected float cellHeight = 30;
    protected float cellHorzPadding = 0;
    protected float cellVertPadding = 4;
    private Table dummyTable;

    @Override public void addActor(Actor actor) {}

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        reposition();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        reposition();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        children.forEach(c -> c.getActor().draw(batch, parentAlpha));
    }

    public void reposition() {
        Vector2 cellIndex = new Vector2();
        children.forEach(c -> {
            if(c.getActor() instanceof NewRow) {
                cellIndex.x = 0;
                cellIndex.y++;
                return;
            }
            final float baseWidth  = (cellWidth  + cellHorzPadding);
            final float baseHeight = (cellHeight + cellVertPadding);

            float w = baseWidth * c.getColspan();
            float h = cellHeight;
            float x = cellIndex.x * baseWidth;
            float y = cellIndex.y * baseHeight;
            c.getActor().setBounds(x, y, w, h);
        });
    }

    public Cell<Actor> add(Actor actor) {
        Cell<Actor> cell = new Cell<>();
        cell.setActor(actor);
        childrenChanged();
        return cell;
    }
    public void row() { add(new NewRow()); }

    public static class NewRow extends Actor {
        @Override public void draw(Batch batch, float parentAlpha) {}
    }


}
