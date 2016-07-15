package net.ncguy.argent.data.config;

import net.ncguy.argent.data.Meta;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Guy on 15/07/2016.
 */
public class ConfigurableAttribute<T> {

    private Meta.Object meta;
    private Supplier<T> getter;
    private Consumer<T> setter;
    private ConfigControl control;
    private Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> params;

    private Function<String, T> castTunnel;

    public ConfigurableAttribute(Meta.Object meta, Supplier<T> getter, Consumer<T> setter, ConfigControl control) {
        this.meta = meta;
        this.getter = getter;
        this.setter = setter;
        this.control = control;
        this.params = new HashMap<>();
    }

    public Function<String, T> castTunnel() { return castTunnel; }
    public void castTunnel(Function<String, T> castTunnel) { this.castTunnel = castTunnel; }

    public String displayName() { return meta.displayName; }
    public String category() { return meta.category; }
    public Meta.Object meta() { return meta; }
    public Supplier<T> getter() { return getter; }
    public Consumer<T> setter() { return setter; }
    public ConfigControl control() { return control; }
    public Map<String, AbstractMap.SimpleEntry<Class<?>, Object>> params() { return params; }

    public T get() { return getter().get(); }
    public void set(T t) { setter().accept(t); }
    public void setSafe(String s) { set(cast(s)); }

    public T cast(String s) {
        if(castTunnel != null)
            return castTunnel.apply(s);
        return (T)s;
    }

    public <U> void addParam(String key, Class<U> type, U value) {
        params.put(key, new AbstractMap.SimpleEntry<>(type, value));
    }

}
