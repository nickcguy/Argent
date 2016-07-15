package net.ncguy.argent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 15/07/2016.
 */
public class Argent {

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



}
