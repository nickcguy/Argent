package net.ncguy.argent.diagnostics.swing;

import net.ncguy.argent.assets.ArgShader;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Guy on 05/09/2016.
 */
public class ShaderViewerController {

    public JFrame frame;
    public ShaderViewer form;

    public ShaderViewerController(Runnable onClose) {
        form = new ShaderViewer();

        frame = new JFrame();
        frame.setContentPane(form.$$$getRootComponent$$$());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setLocationByPlatform(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                onClose.run();
            }
        });

        form.init();
    }


    public void show(ArgShader shader) {
        show(shader.name(), shader.vertexShaderSource, shader.fragmentShaderSource);
    }

    public void show(String name, String vert, String frag) {
        form.ShaderName.setText(name);
        form.VertexShaderSource.setText(vert);
        form.FragmentShaderSource.setText(frag);

        frame.setVisible(true);
    }

}
