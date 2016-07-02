package net.ncguy.argent.editor;

import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.editor.swing.config.ConfigControl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Guy on 22/06/2016.
 */
public class ConfigurableAttribute<T> {

    private String displayName;
    private Optional<Meta> meta;
    private VarRunnables.ReturnRunnable<T> getter;
    private VarRunnables.VarRunnable<T> setter;
    private ConfigControl control;
    private Map<String, BasicEntry<Class<?>, Object>> params;

    public ConfigurableAttribute(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        this.meta = Optional.empty();
        this.displayName = displayName;
        this.getter = getter;
        this.setter = setter;
        this.control = control;
        this.params = new HashMap<>();
    }

    public ConfigurableAttribute(Meta meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        this.meta = Optional.of(meta);
        this.displayName = meta.displayName();
        this.getter = getter;
        this.setter = setter;
        this.control = control;
        this.params = new HashMap<>();
    }

    public String displayName() { return displayName; }
    public Optional<Meta> meta() { return meta; }
    public VarRunnables.ReturnRunnable<T> getter() { return getter; }
    public VarRunnables.VarRunnable<T> setter() { return setter; }
    public ConfigControl control() { return control; }
    public Map<String, BasicEntry<Class<?>, Object>> params() { return params; }
}
