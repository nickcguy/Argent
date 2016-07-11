package net.ncguy.argent.editor.shared.config.builders;

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
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.ui.ColourPickerWrapper;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.data.tree.DataTree;
import net.ncguy.argent.utils.data.tree.PrintIndentedVisitor;

import java.util.List;

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

        fixVisSpinners(spinner);

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

    @Override
    public <T> Object buildColourPicker(ConfigurableAttribute<T> attr) {
        ColourPickerWrapper pickerWrapper = ColourPickerWrapper.instance();
        TextButton btn = new TextButton(attr.displayName()+" - "+attr.get().toString(), skin);
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
        return new Label("Unsupported component", skin);
    }

    @Override
    public void compileSet(Object tree, List<ConfigurableAttribute<?>> attrs) {
        if(!(tree instanceof Tree)) return;
        Tree configTree = (Tree)tree;
        DataTree<AttributeWrapper> wrapperForest = new DataTree<>(new AttributeWrapper(""));
        DataTree<AttributeWrapper> wrapperCurrent = wrapperForest;
        for(ConfigurableAttribute ca : attrs) {
            DataTree<AttributeWrapper> wrapperRoot = wrapperCurrent;
            String path = "";
            String cat = ca.category();
            if(cat.startsWith("|")) cat = cat.substring(1);
            cat += "|"+ca.displayName();
            for(String data : cat.split("\\|")) {
                path += "|"+data;
                AttributeWrapper wrapper;
                wrapper = new AttributeWrapper(data, path);
                wrapperCurrent = wrapperCurrent.child(wrapper);
            }
            wrapperCurrent.data.attr = ca;
            wrapperCurrent = wrapperRoot;
            // TODO compile into tree
//            Object compObj = buildComponent(ca);
//            if (compObj instanceof Actor) {
//                float compWidth = configTable.getParent().getWidth()-192;
//                configTable.add(new Label(ca.displayName(), skin)).width(192);
//                configTable.add((Actor) compObj).width(compWidth-30);
//                configTable.row();
//            }
        }
        wrapperForest.accept(new PrintIndentedVisitor<>(0));
        Tree.Node rootNode = new Tree.Node(new Actor());
        configTree.clearChildren();
        configTree.add(rootNode);

        wrapperForest.accept(new GDXSetBuilderVisitor(configTree, rootNode, skin));
        System.out.println(configTree);
    }

    private void fixVisSpinners(Spinner spinner) {
        VisTextField field = spinner.getTextField();
        field.clearListeners();
        field.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                event.stop();
                spinner.getModel().textChanged();
            }
        });
        field.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    Stage stage = field.getStage();
                    if(stage != null) stage.setScrollFocus(null);
                }
            }
        });
        field.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Stage stage = field.getStage();
                if(stage != null) stage.setScrollFocus(spinner.getTextField());
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

    public static class AttributeWrapper {
        public ConfigurableAttribute attr;
        public String name;
        public String path;

        public AttributeWrapper(String path) {
            this.path = path;
            this.name = path.substring(path.lastIndexOf("|")+1);
            this.attr = null;
        }

        public AttributeWrapper(String name, String path) {
            this.path = path;
            this.name = name;
            this.attr = null;
        }

        public AttributeWrapper(String name, String path, ConfigurableAttribute attr) {
            this.name = name;
            this.path = path;
            this.attr = attr;
        }

        public AttributeWrapper(ConfigurableAttribute attr) {
            this.attr = attr;
            this.name = this.attr.displayName();
            this.path = this.attr.category();
        }

        public AttributeWrapper(ConfigurableAttribute attr, String path) {
            this.attr = attr;
            this.name = this.attr.displayName();
            this.path = path;
        }

        @Override
        public boolean equals(Object obj) {

            if(this == obj) return true;
            if(obj instanceof AttributeWrapper) {
                if(this.path != null) if(this.path.equals(((AttributeWrapper)obj).path)) return true;
                if(this.name != null) if(this.name.equals(((AttributeWrapper)obj).name)) return true;
            }else{
                if(this.path != null) if(this.path.equals(obj)) return true;
                if(this.name != null) if(this.name.equals(obj)) return true;
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Attr: ");
            if(attr == null) sb.append("null");
            else sb.append(attr.toString());

            sb.append(" | ").append("Name: ");
            if(name == null) sb.append("null");
            else sb.append(name);

            sb.append(" | ").append("Path: ");
            if(path == null) sb.append("null");
            else sb.append(path);

            return sb.toString();
        }
    }

}
