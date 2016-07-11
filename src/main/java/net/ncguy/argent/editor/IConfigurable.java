package net.ncguy.argent.editor;

import net.ncguy.argent.core.Meta;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.editor.shared.config.ConfigControl;
import net.ncguy.argent.utils.ReflectionUtils;

import java.util.List;

/**
 * Created by Guy on 21/06/2016.
 */
public interface IConfigurable {

    List<ConfigurableAttribute<?>> getConfigurableAttributes();

    default List<ConfigurableAttribute<?>> getConfigAttrs() {
        final List<ConfigurableAttribute<?>> list = getConfigurableAttributes();

        ReflectionUtils.getAnnotatedFields(this.getClass(), ConfigAttr.class).forEach(f -> {
            ConfigAttr attrData = f.getAnnotation(ConfigAttr.class);
            ConfigurableAttribute<?> confAttr;

            VarRunnables.ReturnRunnable getter = () -> { try { return f.get(this);  } catch (IllegalAccessException e) { e.printStackTrace(); return null; }};
            VarRunnables.VarRunnable setter = (val) -> { try { f.set(this, val); } catch (IllegalAccessException e) { e.printStackTrace(); }};

            if(f.isAnnotationPresent(Meta.class))
                confAttr = attr(new Meta.Object(f.getAnnotation(Meta.class)), getter, setter, attrData.control());
            else
                confAttr = attr(attrData.displayName(), getter, setter, attrData.control());

            list.add(confAttr);
        });

        return list;
    }

    default <T> ConfigurableAttribute<T> attr(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        return new ConfigurableAttribute<>(displayName, getter, setter, control);
    }
    default <T> ConfigurableAttribute<T> attr(Meta.Object meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {

        return new ConfigurableAttribute<>(meta, getter, setter, control);
    }

    default <T> ConfigurableAttribute<T> attr(String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control, VarRunnables.VarReturnRunnable<String, T> castTunnel) {
        ConfigurableAttribute<T> attr = new ConfigurableAttribute<>(displayName, getter, setter, control);;
        attr.castTunnel(castTunnel);
        return attr;
    }
    default <T> ConfigurableAttribute<T> attr(Meta.Object meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control, VarRunnables.VarReturnRunnable<String, T> castTunnel) {
        ConfigurableAttribute<T> attr = new ConfigurableAttribute<>(meta, getter, setter, control);
        attr.castTunnel(castTunnel);
        return attr;
    }

    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> list, String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        ConfigurableAttribute<T> attr = attr(displayName, getter, setter, control);
        list.add(attr);
        return attr;
    }
    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> list, Meta.Object meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control) {
        ConfigurableAttribute<T> attr = attr(meta, getter, setter, control);
        list.add(attr);
        return attr;
    }

    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> list, String displayName, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control, VarRunnables.VarReturnRunnable<String, T> castTunnel) {
        ConfigurableAttribute<T> attr = new ConfigurableAttribute<>(displayName, getter, setter, control);;
        attr.castTunnel(castTunnel);
        list.add(attr);
        return attr;
    }
    default <T> ConfigurableAttribute<T> attr(List<ConfigurableAttribute<?>> list, Meta.Object meta, VarRunnables.ReturnRunnable<T> getter, VarRunnables.VarRunnable<T> setter, ConfigControl control, VarRunnables.VarReturnRunnable<String, T> castTunnel) {
        ConfigurableAttribute<T> attr = new ConfigurableAttribute<>(meta, getter, setter, control);
        attr.castTunnel(castTunnel);
        list.add(attr);
        return attr;
    }


}
