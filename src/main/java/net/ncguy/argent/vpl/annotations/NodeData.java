package net.ncguy.argent.vpl.annotations;

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
    String[] argNames() default "";
    boolean execIn() default true;
    boolean execOut() default true;
    String[] extras() default "";

    NodeColour colour() default @NodeColour(r = 2, g = 2, b = 2, a = 2);

    /**
     * Used to identify {@link net.ncguy.argent.vpl.struct.IdentifierObject} types safely
     * @return
     */
    Class[] outputTypes() default Object.class;

    int outPins() default 1;
}
