package net.ncguy.argent.ui.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.ui.Icons;

/**
 * Created by Guy on 22/09/2016.
 */
public class DataTable extends Table {

    DataTableModel model;
    Drawable headerBG;
    Drawable contentBG;
    float headerBGY = 0;
    float headerBGHeight = 0;



    public DataTable() {
        this(0);
    }

    public DataTable(int columnCount) {
        this(new DataTableModel(columnCount));
    }

    public DataTable(DataTableModel model) {
        super(VisUI.getSkin());
        this.model = model;
        headerBG = new NinePatchDrawable(Icons.Node.TABLEHEADER.patch());
        invalidateModel();
    }

    public void invalidateModel() {
        clearChildren();
        addObjectRow(model.headers);
        row();
        model.getRows().forEach(this::addObjectRow);

        headerBGHeight = this.getRowHeight(0);
        headerBGY = this.getHeight() - headerBGHeight;
    }

    @Override
    public float getRowHeight(int rowIndex) {
        float height;
        try {
            height = super.getRowHeight(rowIndex);
        }catch (Exception e) {
            height = 24;
        }
        return height;
    }

    private void addObject(Object obj) {
        if(obj instanceof Actor) add((Actor)obj);
        else add(obj.toString());
    }

    private void addObjectRow(Object... objs) {
        for (Object obj : objs)
            addObject(obj);
        row();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        headerBG.draw(batch, getX(), getY()+(getHeight()-headerBGHeight), getWidth(), headerBGHeight);
        if(contentBG != null)
            contentBG.draw(batch, 0, 0, getWidth(), getHeight()-headerBGY);
        super.draw(batch, parentAlpha);
    }
}