package net.ncguy.argent.editor.swing.config.descriptors;

import net.ncguy.argent.core.BasicEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public class CheckBoxDescriptor extends ControlConfigDescriptor {


    @Override
    public Map<String, BasicEntry<Class<?>, Object>> attributes() {
        Map<String, BasicEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "selected", Boolean.class, false);
        add(map, "name",     String.class,  "");
        return map;
    }
}
