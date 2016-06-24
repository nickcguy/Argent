package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.utils.StringBuilder;
import com.bulenkov.darcula.DarculaLaf;
import net.ncguy.argent.Argent;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.render.shader.ShaderUtils;
import net.ncguy.argent.utils.FileUtils;
import net.ncguy.argent.utils.Reference;
import net.ncguy.argent.world.GameWorld;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static net.ncguy.argent.utils.Reference.Defaults.Shaders.UNIFORMS;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderForm {

    private JPanel rootPanel;
    private JPanel splitPanel;
    private JTree collectionTree;
    private JButton shaderCompileBtn;
    private JButton newShaderButton;
    private RSyntaxTextArea vertexShaderArea;
    private RSyntaxTextArea fragmentShaderArea;
    private JList<Object> uniformList;
    private JTextField shaderName;
    private JButton deleteShaderBtn;
    private JPanel vertexLabel;
    private JPanel fragmentLabel;
    private JButton applyShaderBtn;
    private JComboBox finalRendererSelect;
    private JTextField shaderDescArea;

    private List<DynamicShader.Info> shaderInfo;
    private DynamicShader.Info selectedShaderInfo;
    private DefaultMutableTreeNode shaderNode;

    private GameWorld.Generic<?> world;

    public static JFrame frame;
    private static ShaderForm form;
    private static Thread ioThread;
    private static Stack<Runnable> ioCalls;

    private static Map<String, Theme> themeMap = new HashMap<>();

    public Runnable onApply;

    static {
        ioCalls = new Stack<>();
        ioThread = new Thread(() -> {
            while(true) {
                try {
                    if(ioCalls.isEmpty()) Thread.sleep(1000);
                    else ioCalls.pop().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ioThread.start();
    }

    public GameWorld.Generic<?> world() { return world; }
    public ShaderForm World(GameWorld.Generic<?> world) {
        this.world = world;
        return this;
    }

    public static JFrame getFrame() {
        if(frame == null)
            getForm();
        return frame;
    }

    public static ShaderForm getForm() {
        if(frame == null) {

            try {
                UIManager.setLookAndFeel(DarculaLaf.class.getCanonicalName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                e.printStackTrace();
            }

            form = new ShaderForm();
            frame = new JFrame("ShaderForm");
            frame.setContentPane(form.rootPanel);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.pack();
            frame.setSize(626, 446);
            frame.setLocationByPlatform(true);
            frame.setLocationRelativeTo(null);
            frame.setVisible(false);

            form.vertexShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
            form.vertexShaderArea.setCodeFoldingEnabled(true);

            form.fragmentShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
            form.fragmentShaderArea.setCodeFoldingEnabled(true);

            try {
                File themeFile = new File("themes");
                System.out.println(themeFile.getAbsolutePath());
                File[] files = themeFile.listFiles();
                for (File file : files) {
                    Theme theme = Theme.load(new FileInputStream(file));
                    themeMap.put(FileUtils.getFileName(file).toLowerCase(), theme);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            form.shaderInfo = new ArrayList();

            form.newShaderButton.addActionListener(e -> form.add());
            form.shaderCompileBtn.addActionListener(e -> {
                form.displayDetectedUniforms();
                form.save();
            });
            form.applyShaderBtn.addActionListener(e -> form.apply());
            form.deleteShaderBtn.addActionListener(e -> form.delete());

            form.uniformList.setModel(new DefaultListModel<>());

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Editor");

            form.collectionTree.setModel(new DefaultTreeModel(rootNode));

            form.collectionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            form.collectionTree.addTreeSelectionListener(e -> form.select());

            DefaultMutableTreeNode autoWireNode = new DefaultMutableTreeNode("Autowired Uniforms");
            UNIFORMS().keySet().stream().sorted((o1, o2) -> o1.alias.compareToIgnoreCase(o2.alias)).forEach(k -> {
                autoWireNode.add(new DefaultMutableTreeNode(k.alias));
            });

            form.shaderNode = new DefaultMutableTreeNode("Shaders");
            form.collectionTree.setModel(new DefaultTreeModel(rootNode));

            rootNode.add(autoWireNode);
            rootNode.add(form.shaderNode);

            form.vertexLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.vertexShaderArea));
            form.fragmentLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.fragmentShaderArea));

            JMenuBar menuBar = new JMenuBar();
            JMenu themeMenu = new JMenu("Theme");
            themeMap.forEach((s, t) -> themeMenu.add(new JMenuItem(new AbstractAction(s) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    t.apply(form.vertexShaderArea);
                    t.apply(form.fragmentShaderArea);
                }
            })));
            menuBar.add(themeMenu);
            frame.setJMenuBar(menuBar);

            form.readFromDisk();
        }
        return form;
    }

    private List<String> getDetectedUniformNames() {
        List<String> names = new ArrayList<>();
        Map<BaseShader.Uniform, BaseShader.Setter> uniforms = ShaderUtils.detectUniforms(vertexShaderArea.getText(), fragmentShaderArea.getText());
        uniforms.keySet().forEach(u -> names.add(u.alias));
        return names;
    }

    private void displayDetectedUniforms() {
        displayItems(getDetectedUniformNames());
    }

    private <T> void displayItems(List<T> items) {
        DefaultListModel<Object> model = (DefaultListModel<Object>) uniformList.getModel();
        model.clear();
        items.forEach(model::addElement);
    }

    public void apply() {
        if(onApply != null) onApply.run();
    }

    private void add() {
        vertexShaderArea.setText(Reference.Defaults.Shaders.VERTEX);
        fragmentShaderArea.setText(Reference.Defaults.Shaders.FRAGMENT);
        shaderName.setText("New Shader");
        selectedShaderInfo = null;
    }

    private void select() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) collectionTree.getLastSelectedPathComponent();
        if(node == null) return;
        Object nodeObj = node.getUserObject();
        if(nodeObj instanceof DynamicShader.Info) {
            DynamicShader.Info info = (DynamicShader.Info)nodeObj;
            selectShader(info);
        }
    }

    private void selectShader(DynamicShader.Info info) {
        shaderName.setText(info.name);
        shaderDescArea.setText(info.desc);
        vertexShaderArea.setText(info.vertex);
        fragmentShaderArea.setText(info.fragment);
        if(info.uniforms == null)
            info.uniforms = getDetectedUniformNames();
        displayItems(info.uniforms);
        selectedShaderInfo = info;
    }

    private void updateSelectionElement() {
        Object selected = finalRendererSelect.getSelectedItem();
        finalRendererSelect.removeAllItems();
        DefaultComboBoxModel model = (DefaultComboBoxModel)finalRendererSelect.getModel();
        shaderInfo.forEach(i -> model.addElement(i));

        int index = model.getIndexOf(selected);
        if(index == -1) return;
        model.setSelectedItem(selected);
    }

    private void save() {
        if(selectedShaderInfo == null) {
            selectedShaderInfo = new DynamicShader.Info();
            shaderNode.add(selectedShaderInfo.treeNode = new DefaultMutableTreeNode(selectedShaderInfo));
            shaderInfo.add(selectedShaderInfo);
        }
        selectedShaderInfo.name = shaderName.getText();
        selectedShaderInfo.desc = shaderDescArea.getText();
        selectedShaderInfo.vertex = vertexShaderArea.getText();
        selectedShaderInfo.fragment = fragmentShaderArea.getText();
        selectedShaderInfo.uniforms = getDetectedUniformNames();

        updateSelectionElement();
        collectionTree.updateUI();

        Gdx.app.postRunnable(selectedShaderInfo::compile);

        write(selectedShaderInfo.copy());
    }

    private void write(final DynamicShader.Info info) {
        writeToFolder("shaders", info);
    }

    private void delete() {
        DynamicShader.Info info = selectedShaderInfo;
        shaderNode.remove(info.treeNode);
        shaderInfo.remove(info);
        updateSelectionElement();
        collectionTree.updateUI();
        writeToFolder("shaderBin", info);
        File file = new File("shaders/"+info.name+".shader");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFolder(final String folder, final DynamicShader.Info info) {
        ioCalls.push(() -> {
            String json = Argent.serial.serialize(info);

            File dir = new File(folder+"/");
            if(!dir.exists()) dir.mkdir();

            File file = new File(folder+"/"+info.name+".shader");
            Path path = file.toPath();
            try {
                Files.deleteIfExists(path);
                Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Stack<DynamicShader.Info> compileToStack() {
        Stack<DynamicShader.Info> infoStack = new Stack<>();
        DynamicShader.Info[] finalShaderInfo = new DynamicShader.Info[]{null};
        Object obj = finalRendererSelect.getSelectedItem();
        if(obj instanceof DynamicShader.Info) finalShaderInfo[0] = (DynamicShader.Info) obj;
        if(finalShaderInfo[0] == null) {
            Argent.log("A final shader needs to be selected", true);
            return null;
        }
        infoStack.push(finalShaderInfo[0]);
        shaderInfo.stream().filter(i -> i != finalShaderInfo[0]).forEach(infoStack::push);
        return infoStack;
    }

    private void readFromDisk() {
        File dir = new File("shaders/");
        if(!dir.exists()) dir.mkdir();
        File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println("Name: "+file.getName());
            String ext = FileUtils.getFileExtension(file);
            System.out.println("Ext: "+ext);
            if(ext.equalsIgnoreCase("shader")) {
                try {
                    DynamicShader.Info info = read(FileUtils.getFileName(file));
                    System.out.println(info);
                    if(info != null) {
                        shaderNode.add(info.treeNode = new DefaultMutableTreeNode(info));
                        shaderInfo.add(info);
                        updateSelectionElement();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        collectionTree.updateUI();
    }

    private DynamicShader.Info read(String name) throws IOException {
        File dir = new File("shaders/");
        if(!dir.exists()) dir.mkdir();

        File file = new File("shaders/"+name+".shader");
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        String json = sb.toString();
        DynamicShader.Info info = Argent.serial.deserialize(json, DynamicShader.Info.class);
        System.out.println(info);
        return info;
    }

    public void createUIComponents() {

    }

    public static class ThemeContext extends JPopupMenu {

        private Map<String, Theme> themeMap;
        private RSyntaxTextArea area;

        public ThemeContext(Map<String, Theme> themeMap, RSyntaxTextArea area) {
            this.themeMap = themeMap;
            this.area = area;
            buildItems();
        }

        private void buildItems() {
            themeMap.forEach((s, t) -> add(new JMenuItem(new AbstractAction(s) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    t.apply(area);
                }
            })));
        }
    }

}
