package net.ncguy.argent.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/**
 * Created by Guy on 15/07/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta {

    String displayName();
    String category() default "";

    class Object {
        public String displayName;
        public String category;

        public Object(String displayName) {
            this(displayName, "");
        }

        public Object(String displayName, String category) {
            this.displayName = displayName;
            this.category = category;
        }

        public Object(Meta meta) {
            this.displayName = meta.displayName();
            this.category = meta.category();
        }

        public static Meta.Object buildFromField(Field field) {
            if(field.isAnnotationPresent(Meta.class)) {
                return new Meta.Object(field.getAnnotation(Meta.class));
            }
            return new Meta.Object(field.getName(), field.getDeclaringClass().getSimpleName());
        }
    }

}
