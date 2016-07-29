package net.ncguy.argent.data.config.descriptors;

import net.ncguy.argent.data.config.ConfigControlDescriptor;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 21/07/2016.
 */
public class NumberSelectorDescriptor extends ConfigControlDescriptor {
    @Override
    public Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> attributes() {
        Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "min", Integer.class, Integer.MIN_VALUE);
        add(map, "max", Integer.class, Integer.MAX_VALUE);
        add(map, "step", Float.class, 1);
        add(map, "precision", Integer.class, 1);
        return map;
    }
}
