package net.ncguy.argent.editor;

import net.ncguy.argent.core.Meta;
import net.ncguy.argent.core.VarRunnables;

import java.util.Optional;

/**
 * Created by Guy on 22/06/2016.
 */
public class ConfigurableAttribute<T> {

    private String displayName;
    private Optional<Meta> meta;
    private VarRunnables.ReturnRunnable<T> getter;
    private VarRunnables.VarRunnable<T> setter;

    public ConfigurableAttribute(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter) {
        this.meta = Optional.empty();
        this.displayName = displayName;
        this.getter = getter;
        this.setter = setter;
    }

    public ConfigurableAttribute(Meta meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter) {
        this.meta = Optional.of(meta);
        this.displayName = meta.displayName();
        this.getter = getter;
        this.setter = setter;
    }

    public String displayName() { return displayName; }
    public Optional<Meta> meta() { return meta; }
    public VarRunnables.ReturnRunnable<T> getter() { return getter; }
    public VarRunnables.VarRunnable<T> setter() { return setter; }
}
