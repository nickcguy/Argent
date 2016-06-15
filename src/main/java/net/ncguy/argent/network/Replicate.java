package net.ncguy.argent.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Guy on 15/06/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Replicate {

    Replication.Event on() default Replication.Event.CHANGE;
    Replication.Target target() default Replication.Target.BROADCAST;

}
