package net.ncguy.argent.editor.builders;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.ui.ColourPickerWrapper;
import net.ncguy.argent.ui.SearchableList;

/**
 * Created by Guy on 21/07/2016.
 */
public class GDXComponentBuilder extends AbstractComponentBuilder {

    private GDXComponentBuilder() {}

    @Override
    public <T> CheckBox buildCheckBox(ConfigurableAttribute<T> attr) {
        String name = getValue(String.class, attr.params().get("name"));
        CheckBox checkBox = new CheckBox(name, VisUI.getSkin());
        checkBox.setChecked((Boolean) attr.get());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                attr.setSafe((checkBox.isChecked()+""));
                checkBox.setChecked((Boolean) attr.get());
            }
        });
        return checkBox;
    }

    @Override
    public <T> TextField buildTextField(ConfigurableAttribute<T> attr) {
        String text = null;
        try {
            text = attr.get().toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        TextField field = new TextField(text, VisUI.getSkin());
        field.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe(field.getText().toString());
                try {
                    field.setText(attr.get().toString());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return field;
    }

    @Override
    public <T> SelectBox buildComboBox(ConfigurableAttribute<T> attr) {
        SelectBox<Object> box = new SelectBox<>(VisUI.getSkin());
//        DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();

        Object[] items = getValue(Object[].class, attr.params().get("items"));
        box.setItems(items);
        box.setSelected(attr.get());
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe(box.getSelected().toString());
                box.setSelected(attr.get());
            }
        });

        return box;
    }

    @Override
    public <T> Spinner buildNumberSelector(ConfigurableAttribute<T> attr) {
        Integer min = getValue(Integer.class, attr.params().get("min"));
        Integer max = getValue(Integer.class, attr.params().get("max"));
        Integer precision = getValue(Integer.class, attr.params().get("precision"));
        Float step = getValue(Float.class, attr.params().get("step"));

        step = .1f;

        SimpleFloatSpinnerModel model = new SimpleFloatSpinnerModel(Float.parseFloat(attr.get().toString()), min, max, step, precision);
        Spinner spinner = new Spinner("", model);

        fixVisSpinners(spinner);

        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float newVal = model.getValue();
                attr.setSafe(newVal+"");
                float oldVal = Float.parseFloat(attr.get().toString());
                model.setValue(oldVal, false);
            }
        });

        return spinner;
    }

    @Override
    public <T> TextButton buildSelectionList(ConfigurableAttribute<T> attr) {
        Object curVal = attr.get();
        if(curVal == null) curVal = new Object();
        TextButton btn = new TextButton(curVal.toString(), VisUI.getSkin());
        SearchableList<String> list = new SearchableList<>();
        SearchableList.Item[] itemData = getValue(SearchableList.Item[].class, attr.params().get("items"));
        list.addItems(itemData);
        list.setChangeListener((item) -> {
            String val = item.value;
            btn.setText(val);
            attr.setSafe(val);
        });
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                list.show(btn.getStage());
                SearchableList.Item<?> item = list.selectedItem();
                if(item == null)
                    btn.setText("N/A");
                else btn.setText(item.value.toString());
            }
        });
        return btn;
    }

    @Override
    public <T> TextButton buildColourPicker(ConfigurableAttribute<T> attr) {
        ColourPickerWrapper pickerWrapper = ColourPickerWrapper.instance();
        TextButton btn = new TextButton(attr.displayName()+" - "+attr.get().toString(), VisUI.getSkin());
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pickerWrapper.setListener(new ColorPickerAdapter() {
                    @Override
                    public void changed(Color newColor) {
                        attr.setSafe(newColor.toString());
                        btn.setText(attr.displayName()+" - "+newColor.toString());
                    }
                });
                pickerWrapper.colour(attr.get().toString());
                pickerWrapper.open(btn);
            }
        });
        return btn;
    }

    @Override
    public <T> Object buildUnsupportedWidget(ConfigurableAttribute<T> attr) {
        return new Label("Unsupported widget type, "+attr.control(), VisUI.getSkin());
    }


    private static GDXComponentBuilder instance;
    public static GDXComponentBuilder instance() {
        if(instance == null) instance = new GDXComponentBuilder();
        return instance;
    }

    private void fixVisSpinners(Spinner spinner) {
        VisTextField textField = spinner.getTextField();
        textField.clearListeners();
        textField.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                event.stop();
                spinner.getModel().textChanged();
            }
        });

        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    Stage s = textField.getStage();
                    if(s != null)
                        s.setScrollFocus(null);
                }
            }
        });

        textField.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Stage s = textField.getStage();
                if(s != null) s.setScrollFocus(textField);
                return true;
            }

            @Override
            public boolean scrolled (InputEvent event, float x, float y, int amount) {
                if (amount == 1) {
                    spinner.decrement();
                } else {
                    spinner.increment();
                }

                return true;
            }

            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    spinner.notifyValueChanged(true);
                    return true;
                }

                return false;
            }
        });
    }
}
