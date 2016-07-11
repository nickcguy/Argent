package net.ncguy.argent.editor.shared.config.descriptors;

import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.ui.SearchableList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 07/07/2016.
 */
public class SelectionListDescriptor extends ControlConfigDescriptor {

    @Override
    public Map<String, BasicEntry<Class<?>, Object>> attributes() {
        Map<String, BasicEntry<Class<?>, Object>> map = new HashMap<>();
        add(map, "items", SearchableList.Item.Data[].class, new SearchableList.Item.Data[0]);
        return map;
    }

}
