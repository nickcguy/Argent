package net.ncguy.argent.editor.shared.config.descriptors;

import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.editor.shared.config.builders.AbstractComponentBuilder;
import net.ncguy.argent.editor.shared.config.builders.SwingComponentBuilder;

import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public abstract class ControlConfigDescriptor {

    public AbstractComponentBuilder componentBuilder = SwingComponentBuilder.instance();

    public abstract Map<String, BasicEntry<Class<?>, Object>> attributes();

    public void add(Map<String, BasicEntry<Class<?>, Object>> m, String k, Class<?> t, Object o) {
        m.put(k, new BasicEntry<>(t, o));
    }

}