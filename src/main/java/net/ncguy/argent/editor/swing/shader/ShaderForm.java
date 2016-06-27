package net.ncguy.argent.editor.swing.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.StringBuilder;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.swing.components.DraggableTreeNode;
import net.ncguy.argent.editor.swing.components.DroppableTreeNode;
import net.ncguy.argent.editor.swing.components.JDraggableTree;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
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
    private JDraggableTree collectionTree;
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
    private JTextField finalRendererText;

    private List<DynamicShader.Info> shaderInfo;
    private DynamicShader.Info selectedShaderInfo;
    private DefaultMutableTreeNode shaderNode;

    private GameWorld.Generic<?> world;
    private ShaderEditor<?> editorController;

    public static JFrame frame;
    public static Thread ioThread;
    private static Stack<Runnable> ioCalls;

    private boolean realtime = false;

    public RSyntaxTextArea vertexShaderArea() {
        return vertexShaderArea;
    }

    public RSyntaxTextArea fragmentShaderArea() {
        return fragmentShaderArea;
    }

    public Runnable onApply;

    public ShaderForm(ShaderEditor<?> editorController) {
        this.editorController = editorController;
        init();
    }

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
        }, "IOThread");
        ioThread.setDaemon(true);
        ioThread.start();
    }

    public boolean realtime() {
        return realtime;
    }

    public ShaderForm realtime(boolean realtime) {
        this.realtime = realtime;
        return this;
    }

    public GameWorld.Generic<?> world() {
        return world;
    }

    public ShaderForm World(GameWorld.Generic<?> world) {
        this.world = world;
        return this;
    }

    public void init() {
        vertexShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        vertexShaderArea.setCodeFoldingEnabled(true);

        fragmentShaderArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        fragmentShaderArea.setCodeFoldingEnabled(true);

        shaderInfo = new ArrayList();

        newShaderButton.addActionListener(e -> add());
        shaderCompileBtn.addActionListener(e -> {
            displayDetectedUniforms();
            save();
        });
        applyShaderBtn.addActionListener(e -> apply());
        deleteShaderBtn.addActionListener(e -> delete());

        uniformList.setModel(new DefaultListModel<>());

        // TREE

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Editor");

//        collectionTree.setModel(new DefaultTreeModel(rootNode));

        collectionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        collectionTree.addTreeSelectionListener(e -> select());

        DefaultMutableTreeNode autoWireNode = new DefaultMutableTreeNode("Autowired Uniforms");
        UNIFORMS().keySet().stream().sorted((o1, o2) -> o1.alias.compareToIgnoreCase(o2.alias)).forEach(k -> {
            autoWireNode.add(new DefaultMutableTreeNode(k.alias));
        });

        shaderNode = new DroppableTreeNode("Shaders");
        DefaultTreeModel model = new DefaultTreeModel(rootNode);

        collectionTree.setModel(model);

        rootNode.add(autoWireNode);
        rootNode.add(shaderNode);

//            form.vertexLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.vertexShaderArea));
//            form.fragmentLabel.setComponentPopupMenu(new ThemeContext(themeMap, form.fragmentShaderArea));

        readFromDisk();

        finalRendererText.setText(getFinalShader().name);

        collectionTree.addTreeSelectionListener(e -> {
            DynamicShader.Info info = getFinalShader();
            finalRendererText.setText(info != null ? info.name : "Error");
            if (realtime)
                if (info != null)
                    Gdx.app.postRunnable(editorController::compile);
        });
    }

    private void shiftNode(DefaultMutableTreeNode node, int offset, int cap, boolean gt) {
        int index = getChildIndex(node);
        System.out.println(index);
        if (index == -1) return;
        if (gt) {
            if (index >= cap) return;
        } else {
            if (index <= cap) return;
        }
        index += offset;
        node.removeFromParent();
        shaderNode.insert(node, index);
        collectionTree.invalidate();
        collectionTree.repaint();
    }

    private int getChildIndex(TreeNode node) {
        if (shaderNode.isNodeChild(node)) {
            int index = 0;
            Enumeration enumeration = shaderNode.children();
            while (enumeration.hasMoreElements()) {
                Object obj = enumeration.nextElement();
                if (obj == node) return index;
                index++;
            }
        }
        return -1;
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
        selectedShaderInfo = info;
        shaderName.setText(info.name);
        shaderDescArea.setText(info.desc);
        vertexShaderArea.setText(info.vertex);
        fragmentShaderArea.setText(info.fragment);
        if (info.uniforms == null)
            info.uniforms = getDetectedUniformNames();
        displayItems(info.uniforms);
    }

    private void updateSelectionElement() {
//        Object selected = finalRendererSelect.getSelectedItem();
//        finalRendererSelect.removeAllItems();
//        DefaultComboBoxModel model = (DefaultComboBoxModel) finalRendererSelect.getModel();
//        shaderInfo.forEach(i -> model.addElement(i));
//
//        int index = model.getIndexOf(selected);
//        if (index == -1) return;
//        model.setSelectedItem(selected);
    }

    private void save() {
        if (selectedShaderInfo == null) {
            selectedShaderInfo = new DynamicShader.Info();
            shaderNode.add(selectedShaderInfo.treeNode = new DraggableTreeNode(selectedShaderInfo));
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
        ShaderProgram prog;
        for (DynamicShader.Info info : shaderInfo) {
            prog = info.compile();
            if (prog == null) return null;
        }

        Stack<DynamicShader.Info> infoStack = new Stack<>();
//        infoStack.push(finalShaderInfo[0]);
        Enumeration children = shaderNode.children();
        List<DynamicShader.Info> infoList = new ArrayList<>();
        while (children.hasMoreElements()) {
            Object nodeObj = children.nextElement();
            if (nodeObj instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeObj;
                DynamicShader.Info info = getInfoFromNode(node);
                infoList.add(info);
            }
        }

        for (int i = infoList.size() - 1; i >= 0; i--) {
            DynamicShader.Info info = infoList.get(i);
            if (info != null)
                infoStack.push(info);
        }
        return infoStack;
    }

    public DynamicShader.Info getFinalShader() {
        DynamicShader.Info info = null;
        TreeNode treeNode = shaderNode.getLastChild();
        if (treeNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
            info = getInfoFromNode(node);
        }
        return info;
    }

    private DynamicShader.Info getInfoFromNode(DefaultMutableTreeNode node) {
        for (DynamicShader.Info info : shaderInfo)
            if (info == node.getUserObject()) {
                return info;
            } else {
                System.out.println("Error");
            }
        return null;
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
                        shaderNode.add(info.treeNode = new DraggableTreeNode(info));
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
        collectionTree = new JDraggableTree();
        scrollPane1.setViewportView(collectionTree);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newShaderButton = new JButton();
        newShaderButton.setText("New Shader");
        panel2.add(newShaderButton, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shaderCompileBtn = new JButton();
        shaderCompileBtn.setText("Compile Shader");
        panel2.add(shaderCompileBtn, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteShaderBtn = new JButton();
        deleteShaderBtn.setText("Delete Shader");
        panel2.add(deleteShaderBtn, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        applyShaderBtn = new JButton();
        applyShaderBtn.setText("Apply Shader");
        panel2.add(applyShaderBtn, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        splitPanel = new JPanel();
        splitPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(splitPanel);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setContinuousLayout(true);
        splitPane2.setOrientation(0);
        splitPane2.setResizeWeight(0.8);
        splitPanel.add(splitPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setContinuousLayout(true);
        splitPane3.setResizeWeight(0.5);
        splitPane2.setLeftComponent(splitPane3);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setLeftComponent(panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        vertexShaderArea = new RSyntaxTextArea();
        vertexShaderArea.setTabsEmulated(false);
        vertexShaderArea.setText("");
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
        shaderDescArea = new JTextField();
        panel8.add(shaderDescArea, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Final Renderer");
        panel8.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        finalRendererText = new JTextField();
        finalRendererText.setEditable(false);
        finalRendererText.setEnabled(true);
        finalRendererText.setText("Shader");
        panel8.add(finalRendererText, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel8.add(spacer1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel8.add(spacer2, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
