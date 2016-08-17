package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * Created by Guy on 27/07/2016.
 */
public class TextFieldWithLabel extends Table {

    private int width = -1;

    protected VisTextField textField;
    private Label label;

    public TextFieldWithLabel(String text, int width) {
        super(VisUI.getSkin());
        this.width = width;
        this.textField = new VisTextField("");
        this.label = new Label(text, VisUI.getSkin());
        setupUI();
    }

    public TextFieldWithLabel(String text) {
        this(text, -1);
    }

    private void setupUI() {
        if(width > 0) {
            add(label).left().width(width * 0.2f);
            add(textField).right().width(width * 0.8f).row();
        }else{
            add(label).left().expandX();
            add(textField).right().expandX().row();
        }
    }

    public String getText() { return textField.getText(); }
    public void setEditable(boolean editable) { textField.setDisabled(!editable); }
    public void clear() { setText(""); }
    public void setText(String text) { textField.setText(text); }
    public void setLabelText(String text) { label.setText(text); }

    @Override
    public boolean addListener(EventListener listener) {
        return textField.addListener(listener);
    }
}
