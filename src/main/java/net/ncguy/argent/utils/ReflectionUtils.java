package net.ncguy.argent.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 02/07/2016.
 */
public class ReflectionUtils {

    public static List<Field> getAllFields(Class<?> cls) {
        List<Field> list = new ArrayList<>();
        Collections.addAll(list, cls.getDeclaredFields());
        if(cls.getSuperclass() != Object.class)
            list.addAll(getAllFields(cls.getSuperclass()));
        return list;
    }

    public static List<Field> getAnnotatedFields(Class<?> cls, Class<? extends Annotation> annotation) {
        return getAllFields(cls).stream().filter(f -> f.isAnnotationPresent(annotation)).collect(Collectors.toList());
    }

    public static Field setCompletelyAccessible(Field field) throws Exception {
        field.setAccessible(true);

        Field modField = Field.class.getDeclaredField("modifiers");
        modField.setAccessible(true);
        modField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
    }

}
