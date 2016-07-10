package net.ncguy.argent.editor.swing.config.descriptors.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.ui.SearchableList;

/**
 * Created by Guy on 04/07/2016.
 */
public class GDXComponentBuilder extends AbstractComponentBuilder {

    private static Skin skin;

    private static GDXComponentBuilder instance;
    public static GDXComponentBuilder instance() {
        if (instance == null)
            instance = new GDXComponentBuilder();
        return instance;
    }

    private GDXComponentBuilder() {
        skin = VisUI.getSkin();
    }

    @Override
    public <T> CheckBox buildCheckBox(ConfigurableAttribute<T> attr) {
        String name = getValue(String.class, attr.params().get("name"));
        CheckBox checkBox = new CheckBox(name, skin);
        checkBox.setChecked((Boolean) attr.get());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe((checkBox.isChecked()+""));
            }
        });
        return checkBox;
    }

    @Override
    public <T> TextField buildTextField(ConfigurableAttribute<T> attr) {
        String text = attr.get().toString();
        TextField field = new TextField(text, skin);
        field.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe(field.getText().toString());
            }
        });
        return field;
    }

    @Override
    public <T> SelectBox buildComboBox(ConfigurableAttribute<T> attr) {
        SelectBox<Object> box = new SelectBox<>(skin);
//        DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();

        Object[] items = getValue(Object[].class, attr.params().get("items"));
        box.setItems(items);
        box.setSelected(attr.get());
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe(box.getSelected().toString());
            }
        });

        return box;
    }



    @Override
    public <T> Spinner buildNumberSelector(ConfigurableAttribute<T> attr) {
        Integer min = getValue(Integer.class, attr.params().get("min"));
        Integer max = getValue(Integer.class, attr.params().get("max"));
        Integer precision = getValue(Integer.class, attr.params().get("precision"));

        SimpleFloatSpinnerModel model = new SimpleFloatSpinnerModel(Float.parseFloat(attr.get().toString()), min, max, 1, precision);

        Spinner spinner = new Spinner("", model);

        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attr.setSafe(model.getValue()+"");
            }
        });

        return spinner;
    }

    @Override
    public <T> TextButton buildSelectionList(ConfigurableAttribute<T> attr) {
        Object curVal = attr.get();
        if(curVal == null) curVal = new Object();
        TextButton btn = new TextButton(curVal.toString(), skin);
        SearchableList<String> list = new SearchableList<>(skin);
        SearchableList.Item.Data[] itemData = getValue(SearchableList.Item.Data[].class, attr.params().get("items"));
        SearchableList.Item[] items = new SearchableList.Item[itemData.length];
        for (int i = 0; i < itemData.length; i++)
            items[i] = itemData[i].compile(skin);
        list.addItems(items);
        list.addChangeListener((item) -> {
            String val = item.value();
            btn.setText(val);
            attr.setSafe(val);
        });
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                list.show(btn.getStage());
            }
        });
        return btn;
    }
}
