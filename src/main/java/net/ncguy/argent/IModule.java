package net.ncguy.argent;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class IModule {

    public void init() {}

    public abstract String moduleName();

    public Class<IModule>[] dependencies() {
        return new Class[0];
    }

    public void log(String format, String... args) {
        log(String.format(format, (Object[]) args));
    }

    public void log(String text) {
        try {
            this.logStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected OutputStream logStream = new ByteArrayOutputStream();

    public OutputStream logStream() {
        return logStream;
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        sb.append(logStream().toString());
        return sb.toString();
    }

}
