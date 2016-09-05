package net.ncguy.argent.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 31/07/2016.
 */
public class ReflectionUtils {

    public static void setValue(Object instance, String fieldName, Object value) {
        Class<?> cls = instance.getClass();
        try {
            Field field = cls.getDeclaredField(fieldName);
            setCompletelyAccessible(field);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Field setCompletelyAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modField = Field.class.getDeclaredField("modifiers");
        modField.setAccessible(true);
        modField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
    }

    public static <T> T getValue(Object instance, Class<?> instanceClass, String fieldName, Class<T> type) {
        try {
            Field field = instanceClass.getDeclaredField(fieldName);
            setCompletelyAccessible(field);
            return (T) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Map<Class<?>, Object> primitiveInstances = new HashMap<>();
    static {
        primitiveInstances.put(Integer.class, 0);
        primitiveInstances.put(int.class, 0);
        primitiveInstances.put(Long.class, 0L);
        primitiveInstances.put(long.class, 0L);
        primitiveInstances.put(float.class, 0F);
        primitiveInstances.put(Float.class, 0F);
        primitiveInstances.put(double.class, 0D);
        primitiveInstances.put(Double.class, 0D);
    }
    public static <T> T newInstance(Class<T> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(primitiveInstances.containsKey(cls))
            return (T) primitiveInstances.get(cls);
        return cls.getConstructor().newInstance();
    }


    private static final Map<String, Class<?>> PRIM = Collections.unmodifiableMap(
        new HashMap<String, Class<?>>(16) {
            {
                for(Class<?> cls : new Class<?>[] {
                    void.class,
                    boolean.class,
                    char.class,
                    byte.class,
                    short.class,
                    int.class,
                    long.class,
                    float.class,
                    double.class
                }) {
                    put(cls.getName(), cls);
                }
            }
        }
    );

    public static Class<?> forName(final String name) throws ClassNotFoundException {
        final Class<?> prim = PRIM.get(name);
        if(prim != null)
            return prim;
        return Class.forName(name);
    }

}
