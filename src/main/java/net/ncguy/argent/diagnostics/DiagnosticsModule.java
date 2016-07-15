package net.ncguy.argent.diagnostics;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 15/07/2016.
 */
public class DiagnosticsModule extends IModule {

    Map<Class<? extends IModule>, OutputStream> moduleStreams = new HashMap<>();

    public DiagnosticsModule() {
    }

    private void getLogStreams() {
        moduleStreams.clear();
        Argent.loadedModules().forEach((cls, mod) -> moduleStreams.put(cls, mod.logStream()));
    }

    OutputStream logStream = new ByteArrayOutputStream();

    @Override
    public String moduleName() {
        return "Diagnostics";
    }

}
