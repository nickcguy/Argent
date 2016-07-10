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
    private Optional<Meta.Object> meta;
    private VarRunnables.ReturnRunnable<T> getter;
    private VarRunnables.VarRunnable<T> setter;
    private ConfigControl control;
    private Map<String, BasicEntry<Class<?>, Object>> params;

    private VarRunnables.VarReturnRunnable<String, T> castTunnel;

    public ConfigurableAttribute(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        this.meta = Optional.empty();
        this.displayName = displayName;
        this.getter = getter;
        this.setter = setter;
        this.control = control;
        this.params = new HashMap<>();
    }

    public ConfigurableAttribute(Meta.Object meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        this.meta = Optional.of(meta);
        this.displayName = meta.displayName();
        this.getter = getter;
        this.setter = setter;
        this.control = control;
        this.params = new HashMap<>();
    }

    public VarRunnables.VarReturnRunnable<String, T> castTunnel() { return castTunnel; }
    public ConfigurableAttribute castTunnel(VarRunnables.VarReturnRunnable<String, T> castTunnel) {
        this.castTunnel = castTunnel;
        return this;
    }

    public String displayName() { return displayName; }
    public Optional<Meta.Object> meta() { return meta; }
    public VarRunnables.ReturnRunnable<T> getter() { return getter; }
    public VarRunnables.VarRunnable<T> setter() { return setter; }
    public ConfigControl control() { return control; }
    public Map<String, BasicEntry<Class<?>, Object>> params() { return params; }

    public T get() { return getter.run(); }
    public void set(T t) { setter.run(t); }

    public void setSafe(String s) { set(cast(s)); }

    public <U> void addParam(String key, Class<U> type, U value) {
        params.put(key, new BasicEntry<>(type, value));
    }

    public T cast(String s) {
        if(castTunnel != null)
            return castTunnel.run(s);
        return (T)s;
    }

}
