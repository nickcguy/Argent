package net.ncguy.argent.pipe;

import net.ncguy.argent.core.VarRunnables;

import java.util.HashMap;

/**
 * Created by Guy on 04/07/2016.
 */
public class ObjectPipe {

    /**
     * Pseudo-reflection helper class
     */

    private static ObjectPipe instance = new ObjectPipe();
    public static ObjectPipe instance() {
        return instance;
    }

    private ObjectPipe() {}

    private HashMap<String, VarRunnables.ReturnRunnable<?>> registry = new HashMap<>();

    public void r(String key, VarRunnables.ReturnRunnable<?> callback) {
        registry.put(key, callback);
    }

    public Object g(String key) {
        return g(key, Object.class);
    }

    public <T> T g(String key, Class<T> type) {
        if(!registry.containsKey(key)) return null;
        return (T) registry.get(key).run();
    }

    public static void register(String key, VarRunnables.ReturnRunnable<?> callback) {
        instance().r(key, callback);
    }
    public static <T> T get(String key, Class<T> type) {
        return instance().g(key, type);
    }
    public static Object get(String key) {
        return instance().g(key);
    }

}
