package net.ncguy.argent.editor.shared.config.builders;

import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.shared.config.descriptors.ControlConfigDescriptor;

import java.util.List;

/**
 * Created by Guy on 01/07/2016.
 */
public abstract class AbstractComponentBuilder {

    public <T> Object buildComponent(ConfigurableAttribute<T> attr) {
        ControlConfigDescriptor descriptor = attr.control().descriptor;
        descriptor.attributes().forEach((k, v) -> {
            if(!attr.params().containsKey(k))
                attr.params().put(k, v);
            else {
                if(!v.getKey().isAssignableFrom(attr.params().get(k).getKey()))
                    attr.params().put(k, v);
            }
        });

        switch(attr.control()) {
            case CHECKBOX:  return buildCheckBox(attr);
            case COMBOBOX:  return buildComboBox(attr);
            case TEXTFIELD: return buildTextField(attr);
            case NUMBERSELECTOR: return buildNumberSelector(attr);
            case SELECTIONLIST: return buildSelectionList(attr);
            case COLOURPICKER: return buildColourPicker(attr);
        }
        return null;
    }

    public abstract <T> Object buildCheckBox(ConfigurableAttribute<T> attr);
    public abstract <T> Object buildTextField(ConfigurableAttribute<T> attr);
    public abstract <T> Object buildComboBox(ConfigurableAttribute<T> attr);
    public abstract <T> Object buildNumberSelector(ConfigurableAttribute<T> attr);
    public abstract <T> Object buildSelectionList(ConfigurableAttribute<T> attr);
    public abstract <T> Object buildColourPicker(ConfigurableAttribute<T> attr);

    public abstract <T> Object buildUnsupportedWidget(ConfigurableAttribute<T> attr);

    public abstract void compileSet(Object table, List<ConfigurableAttribute<?>> attrs);

    public Object getValue(BasicEntry<Class<?>, Object> entry) {
        Class<?> key = entry.getKey();
        Object value = entry.getValue();

        if(value.getClass().isAssignableFrom(key) || key.isAssignableFrom(value.getClass()))
            return value;
        return null;
    }

    public <T> T getValue(Class<T> cls, BasicEntry<Class<?>, Object> entry) {
        Object obj = getValue(entry);
        if(obj == null) return null;
        return (T) obj;
    }

}
