package net.ncguy.argent.data.config;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class ConfigControlDescriptor {

    public abstract Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> attributes();

    public void add(Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> m, String k, Class<?> t, Object o) {
        m.put(k, new AbstractMap.SimpleEntry<>(t, o));
    }

}
