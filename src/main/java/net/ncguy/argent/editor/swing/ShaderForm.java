package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.bulenkov.darcula.DarculaLaf;
import net.ncguy.argent.render.shader.ShaderUtils;
import net.ncguy.argent.utils.Reference;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.util.Map;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderForm {

    private JPanel rootPanel;
    private JPanel splitPanel;
    private JTree tree1;
    private JButton shaderCompileBtn;
    private JButton newShaderButton;
    private RSyntaxTextArea vertexShaderArea;
    private RSyntaxTextArea fragmentShaderArea;
    private JList<Object> uniformList;

    private static JFrame frame;

    public static JFrame getFrame() {
        if(frame == null) {

            try {
                UIManager.setLookAndFeel(DarculaLaf.class.getCanonicalName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                e.printStackTrace();
            }

            ShaderForm form = new ShaderForm();
            frame = new JFrame("ShaderForm");
            frame.setContentPane(form.rootPanel);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setLocationRelativeTo(null);
            frame.setVisible(false);

            form.vertexShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
            form.vertexShaderArea.setCodeFoldingEnabled(true);

            form.fragmentShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
            form.fragmentShaderArea.setCodeFoldingEnabled(true);

            form.newShaderButton.addActionListener(e -> {

                System.out.println("Pre: "+form.vertexShaderArea.getText());

                form.vertexShaderArea.setText(Reference.Defaults.Shaders.VERTEX);
                form.fragmentShaderArea.setText(Reference.Defaults.Shaders.FRAGMENT);

                System.out.println("Post: "+form.vertexShaderArea.getText());
            });

            form.uniformList.setModel(new DefaultListModel<>());

            form.shaderCompileBtn.addActionListener(e -> {
                try {
                    DefaultListModel<Object> model = (DefaultListModel<Object>) form.uniformList.getModel();
                    model.clear();
                    Map<BaseShader.Uniform, BaseShader.Setter> uniforms = ShaderUtils.detectUniforms(form.vertexShaderArea.getText(), form.fragmentShaderArea.getText());
                    uniforms.forEach((u, s) -> {
                        System.out.printf("%s: %s\n", u.toString(), s.toString());
                        model.addElement(new Object() {
                            @Override
                            public String toString() {
                                return u.alias;
                            }
                        });
                    });
                }catch (Exception exc) {
                    exc.printStackTrace();
                }
            });

        }
        return frame;
    }

    public void createUIComponents() {

    }

}
