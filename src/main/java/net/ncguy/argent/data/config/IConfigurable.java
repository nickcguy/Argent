package net.ncguy.argent.data.config;

import net.ncguy.argent.data.Meta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Guy on 15/07/2016.
 */
public interface IConfigurable {

    void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs);

    /**
     * Create a basic {@link ConfigurableAttribute} with the provided accessors
     * @param meta
     * @param getter
     * @param setter
     * @param control
     * @param <T>
     * @return
     */
    default <T> ConfigurableAttribute<T> attr(Meta.Object meta, Supplier<T> getter, Consumer<T> setter, ConfigControl control) {
        return new ConfigurableAttribute<>(meta, getter, setter, control);
    }

    /**
     *
     * @param attrs
     * @param meta
     * @param getter
     * @param setter
     * @param control
     * @param <T>
     * @return
     */
    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> attrs, Meta.Object meta, Supplier<T> getter, Consumer<T> setter, ConfigControl control) {
        ConfigurableAttribute<T> attr = attr(meta, getter, setter, control);
        if(attrs != null) attrs.add(attr);
        return attr;
    }

    /**
     *
     * @param attrs
     * @param meta
     * @param getter
     * @param setter
     * @param control
     * @param castTunnel
     * @param <T>
     * @return
     */
    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> attrs, Meta.Object meta, Supplier<T> getter, Consumer<T> setter, ConfigControl control, Function<String, T> castTunnel) {
        ConfigurableAttribute<T> attr = attr(meta, getter, setter, control);
        attr.castTunnel(castTunnel);
        if(attrs != null) attrs.add(attr);
        return attr;
    }


}