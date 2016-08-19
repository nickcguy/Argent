package net.ncguy.argent;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.ncguy.argent.content.ContentManager;
import net.ncguy.argent.event.EventBus;
import net.ncguy.argent.injector.InjectionModule;
import net.ncguy.argent.ui.UIModule;
import net.ncguy.argent.vpl.VPLManager;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Guy on 15/07/2016.
 */
public class Argent {

    // Global module references

    public static ContentManager content;
    public static EventBus event;
    public static VPLManager vpl;
    public static UIModule ui;
    @Deprecated
    public static InjectionModule injector;

    // Module loading

    private static Map<Class<? extends IModule>, IModule> loadedModules = new HashMap<>();

    public static Map<Class<? extends IModule>, IModule> loadedModules() { return loadedModules; }

    public static void loadModule(IModule module) {
        if(!isModuleLoaded(module.getClass())) {
            for (Class<IModule> dep : module.dependencies()) {
                try {
                    loadModule(dep.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            loadedModules.put(module.getClass(), module);
            module.init();
        }
    }

    public static boolean isModuleLoaded(IModule module) {
        return isModuleLoaded(module.getClass());
    }
    public static boolean isModuleLoaded(Class<? extends IModule> moduleCls) {
        return loadedModules.containsKey(moduleCls);
    }

    public static IModule getModule(Class<? extends IModule> cls) {
        if(isModuleLoaded(cls)) return loadedModules.get(cls);
        return null;
    }

    // Callbacks

    public static Set<BiConsumer<Integer, Integer>> onResizeListeners = new LinkedHashSet<>();
    public static void onResize(BiConsumer<Integer, Integer> listener) {
        if(onResizeListeners.contains(listener)) removeOnResize(listener);
        else addOnResize(listener);
    }
    public static void addOnResize(BiConsumer<Integer, Integer> listener) {
        onResizeListeners.add(listener);
    }
    public static void removeOnResize(BiConsumer<Integer, Integer> listener) {
        onResizeListeners.remove(listener);
    }
    public static void onResize(final int width, final int height) {
        onResizeListeners.forEach(c -> c.accept(width, height));
    }

    public static Set<Consumer<Integer>> keyListeners = new LinkedHashSet<>();
    public static void onKeyDown(Consumer<Integer> listener) {
        if(keyListeners.contains(listener)) removeOnKeyDown(listener);
        else addOnKeyDown(listener);
    }
    public static void addOnKeyDown(Consumer<Integer> listener) {
        keyListeners.add(listener);
    }
    public static void removeOnKeyDown(Consumer<Integer> listener) {
        keyListeners.remove(listener);
    }
    public static void onKeyDown(int keycode) {
        keyListeners.forEach(c -> c.accept(keycode));
    }

    public static final InputListener globalListener = new InputListener() {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            onKeyDown(keycode);
            return super.keyDown(event, keycode);
        }
    };


    private static List<String> boolVars = new ArrayList<>();
    public static void addBoolVar(String key) {
        boolVars.add(key);
    }
    public static void removeBoolVar(String key) {
        boolVars.remove(key);
    }
    public static void toggleBoolVar(String key) {
        if(hasBoolVar(key)) removeBoolVar(key);
        else addBoolVar(key);
    }
    public static boolean hasBoolVar(String key) {
        return boolVars.contains(key);
    }

    public static class VarKeys {
        public static final String bool_LIGHTDEBUG = "light.debug";
        public static final String bool_SHADOWS = "light.shadow";
    }

}
