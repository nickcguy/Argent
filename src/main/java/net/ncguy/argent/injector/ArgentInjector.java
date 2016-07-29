package net.ncguy.argent.injector;

import net.ncguy.argent.Argent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 27/07/2016.
 */
public class ArgentInjector {

    public static void inject(Object obj) {
        List<Field> injectableFields = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        getAllFields(fields, obj.getClass());
        injectableFields.addAll(fields.stream().filter(field -> field.isAnnotationPresent(Inject.class)).collect(Collectors.toList()));

        try{
            for (Field f : injectableFields)
                injectField(obj, f);
        }catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
    }

    private static void injectField(Object obj, Field field) throws IllegalAccessException {
        for(Field f : InjectionStore.class.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers())) {
                if(f.getType().equals(field.getType())) {
                    f.setAccessible(true);
                    Object value = f.get(null);
                    if(value == null) {
                        Argent.getModule(InjectionModule.class).log("Value in field %s (Requested by Object %s) is null", f.getName(), obj.getClass().getSimpleName());
                    }
                    field.setAccessible(true);
                    field.set(obj, value);
                }
            }
        }
    }

    private static void getAllFields(List<Field> list, Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Collections.addAll(list, fields);
        if(cls.getSuperclass() != Object.class) {
            getAllFields(list, cls.getSuperclass());
        }
    }

}
