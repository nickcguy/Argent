package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.List;

/**
 * Created by Guy on 21/07/2016.
 */
public class NameComponent implements Component, IConfigurable {

    public String name;

    public NameComponent(String name) {
        this.name = name;
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        attr(attrs, new Meta.Object("Name", "Meta"), () -> name, (s) -> name = s, ConfigControl.TEXTFIELD);
    }

}
