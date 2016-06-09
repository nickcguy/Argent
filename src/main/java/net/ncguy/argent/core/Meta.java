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

}
