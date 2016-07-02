package net.ncguy.argent.editor.swing.config.descriptors;

import net.ncguy.argent.core.BasicEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public class ComboBoxDescriptor extends ControlConfigDescriptor {

    @Override
    public Map<String, BasicEntry<Class<?>, Object>> attributes() {
        Map<String, BasicEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "items", List.class, new ArrayList<>());
        add(map, "selectedindex", Integer.class, 0);
        return map;
    }
}
