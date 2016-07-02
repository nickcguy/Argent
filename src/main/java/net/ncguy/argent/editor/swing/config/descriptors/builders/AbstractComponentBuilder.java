package net.ncguy.argent.editor.swing.config.descriptors.builders;

import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.editor.swing.config.ConfigControl;
import net.ncguy.argent.editor.swing.config.descriptors.ControlConfigDescriptor;

import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public abstract class AbstractComponentBuilder {

    public Object buildComponent(ConfigControl control, Map<String, BasicEntry<Class<?>, Object>> params) {
        ControlConfigDescriptor descriptor = control.descriptor;
        descriptor.attributes().forEach((k, v) -> {
            if(!params.containsKey(k))
                params.put(k, v);
        });

        switch(control) {
            case CHECKBOX:  return buildCheckBox(params);
            case COMBOBOX:  return buildComboBox(params);
            case TEXTFIELD: return buildTextField(params);
            case NUMBERSELECTOR: return buildNumberSelector(params);
        }
        return null;
    }

    public abstract Object buildCheckBox(Map<String, BasicEntry<Class<?>, Object>> params);
    public abstract Object buildTextField(Map<String, BasicEntry<Class<?>, Object>> params);
    public abstract Object buildComboBox(Map<String, BasicEntry<Class<?>, Object>> params);
    public abstract Object buildNumberSelector(Map<String, BasicEntry<Class<?>, Object>> params);

    public Object getValue(BasicEntry<Class<?>, Object> entry) {
        Class<?> key = entry.getKey();
        Object value = entry.getValue();

        if(value.getClass().isAssignableFrom(key))
            return value;
        return null;
    }

    public <T> T getValue(Class<T> cls, BasicEntry<Class<?>, Object> entry) {
        Object obj = getValue(entry);
        if(obj == null) return null;
        return (T) obj;
    }

}
