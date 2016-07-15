package net.ncguy.argent;

import net.ncguy.argent.content.ContentManager;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Guy on 15/07/2016.
 */
public class Argent {

    // Global module references

    public static ContentManager content;

    // Module loading

    private static Map<Class<? extends IModule>, IModule> loadedModules = new HashMap<>();

    public static Map<Class<? extends IModule>, IModule> loadedModules() { return loadedModules; }

    public static void loadModule(IModule module) {
        if(!loadedModules.containsKey(module.getClass())) {
            for (Class<IModule> dep : module.dependencies()) {
                try {
                    loadModule(dep.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            loadedModules.put(module.getClass(), module);
        }
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

}
