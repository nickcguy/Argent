package net.ncguy.argent.vpl.inspector;

/**
 * Created by Guy on 09/06/2016.
 */
public interface IInspector {

    String inspect();
    default boolean isValid() {
        if(inspect() == null) return true;
        return inspect().length() == 0;
    }

}
