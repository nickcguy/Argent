package net.ncguy.argent.world;

import net.ncguy.argent.IModule;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Guy on 15/07/2016.
 */
public class WorldModule implements IModule {

    OutputStream logStream = new ByteArrayOutputStream();

    @Override
    public OutputStream logStream() {
        return logStream;
    }
}
