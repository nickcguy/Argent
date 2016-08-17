package net.ncguy.argent.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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


}
