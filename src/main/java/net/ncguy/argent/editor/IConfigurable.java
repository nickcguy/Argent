package net.ncguy.argent.editor;

import net.ncguy.argent.core.Meta;
import net.ncguy.argent.core.VarRunnables;

import java.util.List;

/**
 * Created by Guy on 21/06/2016.
 */
public interface IConfigurable {

    List<ConfigurableAttribute> getConfigurableAttributes();


    default <T> ConfigurableAttribute attr(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter) {
        return new ConfigurableAttribute<>(displayName, getter, setter);
    }
    default <T> ConfigurableAttribute attr(Meta meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter) {
        return new ConfigurableAttribute<>(meta, getter, setter);
    }

}
