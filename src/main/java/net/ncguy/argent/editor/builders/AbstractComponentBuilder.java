package net.ncguy.argent.editor.builders;

import net.ncguy.argent.data.config.ConfigControlDescriptor;
import net.ncguy.argent.data.config.ConfigurableAttribute;

import java.util.AbstractMap;

/**
 * Created by Guy on 21/07/2016.
 */
public abstract class AbstractComponentBuilder {

    public <T> Object buildComponent(ConfigurableAttribute<T> attr) {
        ConfigControlDescriptor descriptor = attr.control().descriptor;
        descriptor.attributes().forEach((k, v) -> {
            if (!attr.params().containsKey(k))
                attr.params().put(k, v);
            else {
                if (!v.getKey().isAssignableFrom(attr.params().get(k).getKey()))
                    attr.params().put(k, v);
            }
        });

        switch (attr.control()) {
            case CHECKBOX:
                return buildCheckBox(attr);
            case COMBOBOX:
                return buildComboBox(attr);
            case TEXTFIELD:
                return buildTextField(attr);
            case NUMBERSELECTOR:
                return buildNumberSelector(attr);
            case SELECTIONLIST:
                return buildSelectionList(attr);
            case COLOURPICKER:
                return buildColourPicker(attr);
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


    public Object getValue(AbstractMap.SimpleEntry<Class<?>, Object> entry) {
        Class<?> key = entry.getKey();
        Object value = entry.getValue();

        if (value.getClass().isAssignableFrom(key) || key.isAssignableFrom(value.getClass()))
            return value;
        return null;
    }

    public <T> T getValue(Class<T> cls, AbstractMap.SimpleEntry<Class<?>, Object> entry) {
        Object obj = getValue(entry);
        if (obj == null) return null;
        return (T) obj;
    }

}

