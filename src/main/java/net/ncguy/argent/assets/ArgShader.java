package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.kryo.KryoManager;
import net.ncguy.argent.diagnostics.DiagnosticsModule;
import net.ncguy.argent.editor.project.Registry;
import net.ncguy.argent.event.StringPacketEvent;
import net.ncguy.argent.event.shader.ShaderSelectedEvent;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.compiler.ShaderProgramCompiler;
import net.ncguy.argent.vpl.nodes.shader.FinalShaderNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 31/08/2016.
 */
public class ArgShader extends ArgAsset<ShaderProgram> {

    static ShaderProgramCompiler compiler;

    static ShaderProgramCompiler compiler() {
        if(compiler == null)
            compiler = new ShaderProgramCompiler();
        return compiler;
    }

    public String name;

    public String vertexShaderSource;
    public String fragmentShaderSource;

    private MenuItem viewGeneratedSource;
    private ArgentShader shader;

    public VPLGraph graph;

    public List<AbstractArgShaderVariable> variables;

    public ArgShader(ShaderProgram program) {
        this(program.getVertexShaderSource(), program.getFragmentShaderSource());
        setAsset(program);
    }

    public ArgShader(String vert, String frag) {
        this(vert, frag, new VPLGraph());
    }

    public ArgShader(String vert, String frag, VPLGraph graph) {
        this.vertexShaderSource = vert;
        this.fragmentShaderSource = frag;
        this.graph = graph;
        variables = new ArrayList<>();
    }

    public VPLNode getGraphRootNode() {
        List<VPLNode> eligibleNodes = graph.nodes.stream().filter(node -> node instanceof FinalShaderNode).collect(Collectors.toList());
        if(eligibleNodes.size() > 0)
            return eligibleNodes.get(0);
        return null;
    }

    public static ArgShader load(File file) {
        if(!file.exists()) return null;
        try{
            ArgShader shader = KryoManager.kryoManager().load(file, ArgShader.class);
            return shader != null ? shader : null;
        }catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        return null;
    }

    public void save() {
        save(new File(getProjectManager().current().getShaderPath() + (this.fileName = name()) + Registry.SHADER_EXT));
    }

    public void save(File file) {
        file.getParentFile().mkdirs();
        try{
            KryoManager.kryoManager().save(file, this);
            System.out.println("Saved to "+file.getAbsolutePath());
        }catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    @Override
    public ShaderProgram getAsset() {
        if(asset == null)
            asset = AppUtils.Shader.compileShader(vertexShaderSource, fragmentShaderSource);
        return super.getAsset();
    }

    @Override
    public String tag() {
        return "shader";
    }

    @Override
    public String name() {
        return this.name != null ? this.name : "Shader";
    }

    @Override
    public void name(String name) {
        this.name = name;
    }

    @Override
    public PopupMenu contextMenu() {
        PopupMenu menu = new PopupMenu() {
            @Override
            public void showMenu(Stage stage, float x, float y) {
                assertSourceItem();
                super.showMenu(stage, x, y);
            }
        };

        menu.addItem(new MenuItem("Select", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new ShaderSelectedEvent(ArgShader.this).fire();
            }
        }));
        menu.addItem(new MenuItem("Save", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                save();
            }
        }));
        menu.addItem(new MenuItem("Compile and Save", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                invalidateAndSave();
            }
        }));
        viewGeneratedSource = new MenuItem("View Generated Source", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                invalidate();
                if (!Argent.isModuleLoaded(DiagnosticsModule.class))
                    Argent.loadModule(new DiagnosticsModule());
                Argent.diagnostics.invokeShaderViewer(viewer -> viewer.show(ArgShader.this));
            }
        });

        menu.addItem(viewGeneratedSource);
        return menu;
    }

    public void invalidate() {
        VPLNode rootNode = getGraphRootNode();
        if(rootNode == null) {
            new StringPacketEvent("toast|info", "No root node found").fire();
            return;
        }
        compiler().compile(graph, rootNode, ArgShader.this);
        if(this.shader != null) {
            this.shader.dispose();
            this.shader = null;
        }
        if(asset != null) {
            asset.dispose();
            asset = null;
        }
        ArgentShaderProvider.clearCache();
    }

    public void invalidateAndSave() {
        invalidate();
        save();
    }

    public ShaderProgram shaderProgram() {
        if(asset == null) {
            if(vertexShaderSource.isEmpty() || fragmentShaderSource.isEmpty()) {
                VPLNode rootNode = getGraphRootNode();
                if (rootNode == null) {
                    new StringPacketEvent("toast|info", "No root node found").fire();
                    return null;
                }
                compiler().compile(graph, rootNode, this);
            }
            asset = new ShaderProgram(vertexShaderSource, fragmentShaderSource);
        }
        return asset;
    }

    private void assertSourceItem() {
        VPLNode rootNode = getGraphRootNode();
        if(rootNode == null) {
            new StringPacketEvent("toast|info", "No root node found").fire();
            return;
        }
        compiler().compile(graph, rootNode, ArgShader.this);
        viewGeneratedSource.setDisabled(vertexShaderSource.isEmpty() || fragmentShaderSource.isEmpty());
    }

}
