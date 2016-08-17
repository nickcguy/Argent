package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSelectBox;

/**
 * Created by Guy on 04/08/2016.
 */
public class EnumSelectionWithLabel<T extends Enum<T>> extends Table {

    private int width = -1;

    protected VisSelectBox<T> select;
    private Label label;
    private Class<T> enumCls;

    public EnumSelectionWithLabel(String text, int width, Class<T> enumCls) {
        super(VisUI.getSkin());
        this.width = width;
        this.enumCls = enumCls;
        this.select = new VisSelectBox<>();
        this.label = new Label(text, VisUI.getSkin());
        this.select.setItems(enumCls.getEnumConstants());
        this.select.setSelectedIndex(0);
        setupUI();
    }

    public EnumSelectionWithLabel(String text, Class<T> enumCls) { this(text, -1, enumCls); }

    private void setupUI() {
        if(width > 0) {
            add(label).left().width(width * 0.2f);
            add(select).right().width(width * 0.8f).row();
        }else{
            add(label).left().expandX();
            add(select).right().expandX().row();
        }
    }

    public T getSelected() { return select.getSelected(); }
    public void setSelected(T sel) { select.setSelected(sel); }
    public void setLabelText(String text) { label.setText(text); }
}
