package net.ncguy.argent.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Guy on 09/06/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta {

    String displayName();
    String category() default "";

    class Object {

        public String displayName;
        public String category;

        public Object(Meta meta) {
            this(meta.displayName(), meta.category());
        }

        public Object(String displayName, String category) {
            this.displayName = displayName;
            this.category = category;
        }

        public String displayName() { return displayName; }
        public String category() { return category; }
    }

}
