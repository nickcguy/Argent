package net.ncguy.argent.diagnostics;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;
import net.ncguy.argent.diagnostics.swing.ShaderViewerController;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Guy on 15/07/2016.
 */
public class DiagnosticsModule extends IModule {

    Map<Class<? extends IModule>, OutputStream> moduleStreams = new HashMap<>();

    private ShaderViewerController shaderViewer;

    @Override
    public void init() {
        Argent.diagnostics = this;
    }

    private ShaderViewerController shaderViewer() {
        if(shaderViewer == null)
            shaderViewer = new ShaderViewerController(this::onClose);
        return shaderViewer;
    }

    private void onClose() {
        shaderViewer = null;
    }

    public void invokeShaderViewer(Consumer<ShaderViewerController> task) {
        SwingUtilities.invokeLater(() -> task.accept(shaderViewer()));
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
