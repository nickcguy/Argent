package net.ncguy.argent.vpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Guy on 19/08/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface NodeData {

    String value();
    String category() default "";
    String[] tags() default "*";
    String[] keywords() default "";
    boolean execIn() default true;
    boolean execOut() default true;

}
