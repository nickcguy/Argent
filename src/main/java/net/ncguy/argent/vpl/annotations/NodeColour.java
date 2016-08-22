package net.ncguy.argent.vpl.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Guy on 22/08/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NodeColour {

    float r() default 1;
    float g() default 1;
    float b() default 1;
    float a() default 1;

}
