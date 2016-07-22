package net.ncguy.argent.data.config.descriptors;

import net.ncguy.argent.data.config.ConfigControlDescriptor;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 21/07/2016.
 */
public class ComboBoxDescriptor extends ConfigControlDescriptor {
    @Override
    public Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> attributes() {
        Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "items", Object[].class, new Object[0]);
        return map;
    }
}
