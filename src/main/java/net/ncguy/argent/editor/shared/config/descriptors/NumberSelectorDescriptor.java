package net.ncguy.argent.editor.shared.config.descriptors;

import net.ncguy.argent.core.BasicEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public class NumberSelectorDescriptor extends ControlConfigDescriptor {


    @Override
    public Map<String, BasicEntry<Class<?>, Object>> attributes() {
        Map<String, BasicEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "min", Integer.class, Integer.MIN_VALUE);
        add(map, "max", Integer.class, Integer.MAX_VALUE);
        add(map, "precision", Integer.class, 0);
        return map;
    }
}
