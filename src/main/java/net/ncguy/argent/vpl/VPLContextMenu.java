package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import net.ncguy.argent.ui.SearchableList;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLContextMenu extends SearchableList<Method> {

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
            Item<Method> item = new Item<>(null, VPLManager.instance().getDisplayName(m), m, VPLManager.instance().getKeywords(m));
            addItem(item);
        });
    }

    private OrthographicCamera camera() {
        return (OrthographicCamera) vplGraph.getStage().getCamera();
    }

}
