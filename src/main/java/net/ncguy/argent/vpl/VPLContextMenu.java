package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.vpl.compiler.ShaderProgramCompiler;
import net.ncguy.argent.vpl.nodes.shader.FinalShaderNode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLContextMenu extends SearchableList<Object> {

    private VPLGraph vplGraph;

    public VPLContextMenu(VPLGraph vplGraph) {
        super();
        this.vplGraph = vplGraph;
        this.keepWithinStage = false;
    }

    @Override
    public void show(Stage stage, float x, float y) {
        setScale(camera().zoom);
        super.show(stage, x, y);
    }

    public void setMethods(String... tags) {
        setMethods(VPLManager.instance().getNodesWithTags(tags));
    }

    public void setMethods(List<Method> methods) {
        clearItems();
        methods.stream().sorted((m1, m2) -> {
            String s1 = VPLManager.instance().getDisplayName(m1);
            String s2 = VPLManager.instance().getDisplayName(m2);
            return s1.compareToIgnoreCase(s2);
        }).forEach(m -> {
            Item<Object> item = new Item<>(null, VPLManager.instance().getDisplayName(m), m, VPLManager.instance().getKeywords(m));
            addItem(item);
        });
    }

    private OrthographicCamera camera() {
        return (OrthographicCamera) vplGraph.getStage().getCamera();
    }

    @Override
    public void preListWidget() {
        TextButton button = new TextButton("Compile Shader", VisUI.getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(vplGraph.compiler == null)
                    vplGraph.compiler = new ShaderProgramCompiler();
                VPLNode node;
                List<VPLNode> collect = vplGraph.nodes.stream().filter(n -> n instanceof FinalShaderNode).collect(Collectors.toList());
                if(collect.size() <= 0) return;
                node = collect.get(0);
                if(node != null)
                    vplGraph.compiler.compile(vplGraph, node);
            }
        });
        add(button).growX().row();
    }
}
