package net.ncguy.argent.editor;

import net.ncguy.argent.editor.swing.config.ConfigControl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Guy on 02/07/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigAttr {

    String displayName() default "";
    ConfigControl control();

}
