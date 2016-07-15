package net.ncguy.argent;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Guy on 15/07/2016.
 */
public interface IModule {

    default String moduleName() {
        return getClass().getSimpleName();
    }

    default Class<IModule>[] dependencies() {
        Class<IModule>[] deps = new Class[0];
        return deps;
    }

    default void log(String text) {
        try {
            this.logStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    OutputStream logStream();

    default String log() {
        StringBuilder sb = new StringBuilder();
        sb.append(logStream().toString());
        return sb.toString();
    }

}
