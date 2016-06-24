package net.ncguy.argent.editor.swing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.StringBuilder;
import com.bulenkov.darcula.DarculaLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

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
            while (true) {
                try {
                    if (ioCalls.isEmpty()) Thread.sleep(1000);
                    else ioCalls.pop().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ioThread.start();
    }

    public GameWorld.Generic<?> world() {
        return world;
    }

    public ShaderForm World(GameWorld.Generic<?> world) {
        this.world = world;
        return this;
    }

    public static JFrame getFrame() {
        if (frame == null)
            getForm();
        return frame;
    }

    public static ShaderForm getForm() {
        if (frame == null) {

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

//            form.vertexLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.vertexShaderArea));
//            form.fragmentLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.fragmentShaderArea));

            form.readFromDisk();

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
        if (onApply != null) onApply.run();
    }

    private void add() {
        vertexShaderArea.setText(Reference.Defaults.Shaders.VERTEX);
        fragmentShaderArea.setText(Reference.Defaults.Shaders.FRAGMENT);
        shaderName.setText("New Shader");
        selectedShaderInfo = null;
    }

    private void select() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) collectionTree.getLastSelectedPathComponent();
        if (node == null) return;
        Object nodeObj = node.getUserObject();
        if (nodeObj instanceof DynamicShader.Info) {
            DynamicShader.Info info = (DynamicShader.Info) nodeObj;
            selectShader(info);
        }
    }

    private void selectShader(DynamicShader.Info info) {
        shaderName.setText(info.name);
        shaderDescArea.setText(info.desc);
        vertexShaderArea.setText(info.vertex);
        fragmentShaderArea.setText(info.fragment);
        if (info.uniforms == null)
            info.uniforms = getDetectedUniformNames();
        displayItems(info.uniforms);
        selectedShaderInfo = info;
    }

    private void updateSelectionElement() {
        Object selected = finalRendererSelect.getSelectedItem();
        finalRendererSelect.removeAllItems();
        DefaultComboBoxModel model = (DefaultComboBoxModel) finalRendererSelect.getModel();
        shaderInfo.forEach(i -> model.addElement(i));

        int index = model.getIndexOf(selected);
        if (index == -1) return;
        model.setSelectedItem(selected);
    }

    private void save() {
        if (selectedShaderInfo == null) {
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
        File file = new File("shaders/" + info.name + ".shader");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFolder(final String folder, final DynamicShader.Info info) {
        ioCalls.push(() -> {
            String json = Argent.serial.serialize(info);

            File dir = new File(folder + "/");
            if (!dir.exists()) dir.mkdir();

            File file = new File(folder + "/" + info.name + ".shader");
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
        DynamicShader.Info[] finalShaderInfo = new DynamicShader.Info[]{null};
        Object obj = finalRendererSelect.getSelectedItem();
        if (obj instanceof DynamicShader.Info) finalShaderInfo[0] = (DynamicShader.Info) obj;
        if (finalShaderInfo[0] == null) {
            Argent.log("A final shader needs to be selected", true);
            return null;
        }

        ShaderProgram prog = null;
        for (DynamicShader.Info info : shaderInfo) {
            prog = info.compile();
            if (prog == null) return null;
        }
        prog = finalShaderInfo[0].compile();
        if (prog == null) return null;

        Stack<DynamicShader.Info> infoStack = new Stack<>();
        infoStack.push(finalShaderInfo[0]);
        shaderInfo.stream().filter(i -> i != finalShaderInfo[0]).forEach(infoStack::push);
        return infoStack;
    }

    private void readFromDisk() {
        File dir = new File("shaders/");
        if (!dir.exists()) dir.mkdir();
        File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println("Name: " + file.getName());
            String ext = FileUtils.getFileExtension(file);
            System.out.println("Ext: " + ext);
            if (ext.equalsIgnoreCase("shader")) {
                try {
                    DynamicShader.Info info = read(FileUtils.getFileName(file));
                    System.out.println(info);
                    if (info != null) {
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
        if (!dir.exists()) dir.mkdir();

        File file = new File("shaders/" + name + ".shader");
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setContinuousLayout(true);
        splitPane1.setOneTouchExpandable(false);
        splitPane1.setOrientation(1);
        splitPane1.setResizeWeight(0.15);
        rootPanel.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        collectionTree = new JTree();
        collectionTree.setRootVisible(true);
        collectionTree.setShowsRootHandles(false);
        scrollPane1.setViewportView(collectionTree);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newShaderButton = new JButton();
        newShaderButton.setText("New Shader");
        panel2.add(newShaderButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shaderCompileBtn = new JButton();
        shaderCompileBtn.setText("Compile Shader");
        panel2.add(shaderCompileBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteShaderBtn = new JButton();
        deleteShaderBtn.setText("Delete Shader");
        panel2.add(deleteShaderBtn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        applyShaderBtn = new JButton();
        applyShaderBtn.setText("Apply Shader");
        panel2.add(applyShaderBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        splitPanel = new JPanel();
        splitPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(splitPanel);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setContinuousLayout(true);
        splitPane2.setOrientation(0);
        splitPane2.setResizeWeight(0.8);
        splitPanel.add(splitPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setResizeWeight(0.5);
        splitPane2.setLeftComponent(splitPane3);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setLeftComponent(panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        vertexShaderArea = new RSyntaxTextArea();
        vertexShaderArea.setTabsEmulated(false);
        vertexShaderArea.setText("asdfgh");
        vertexShaderArea.setUseSelectedTextColor(true);
        scrollPane2.setViewportView(vertexShaderArea);
        vertexLabel = new JPanel();
        vertexLabel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(vertexLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Vertex");
        vertexLabel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setRightComponent(panel4);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel4.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        fragmentShaderArea = new RSyntaxTextArea();
        scrollPane3.setViewportView(fragmentShaderArea);
        fragmentLabel = new JPanel();
        fragmentLabel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(fragmentLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Fragment");
        fragmentLabel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setRightComponent(panel5);
        final JSplitPane splitPane4 = new JSplitPane();
        splitPane4.setContinuousLayout(true);
        splitPane4.setDividerSize(10);
        splitPane4.setResizeWeight(0.5);
        panel5.add(splitPane4, new GridConstraints(0, 0, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane4.setRightComponent(panel6);
        final JLabel label3 = new JLabel();
        label3.setName("");
        label3.setText("Autowired uniforms");
        panel6.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uniformList = new JList();
        uniformList.setName("");
        panel6.add(uniformList, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane4.setLeftComponent(panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Shader Name");
        panel8.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shaderName = new JTextField();
        panel8.add(shaderName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Shader Description");
        panel8.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        finalRendererSelect = new JComboBox();
        panel8.add(finalRendererSelect, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shaderDescArea = new JTextField();
        panel8.add(shaderDescArea, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label6 = new JLabel();
        label6.setText("Final Renderer");
        panel8.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel8.add(spacer1, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
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
