package net.ncguy.argent.vpl;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLGraph extends Group {

    List<VPLNode> nodes;
    List<Method> nodeMethods;
    String[] tags;
    VPLContextMenu menu;
    Vector2 pos = new Vector2();
    public Rectangle bounds;

    public VPLGraph(String... tags) {
        this.tags = tags;
        this.nodes = new ArrayList<>();
        this.nodeMethods = VPLManager.instance().getNodesWithTags(tags);
        this.menu = new VPLContextMenu(this);
        this.menu.setMethods(this.nodeMethods);
        this.menu.setChangeListener(item -> {
            Method m = item.value;
            addNode(m);
        });
        addListener(menuListener);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void addNode(VPLNode node) {
        this.nodes.add(node);
        addActor(node);
        node.setPosition(pos.x, pos.y);
    }
    public void addNode(Method method) {
        addNode(createNode(method));
    }
    public VPLNode createNode(Method method) {
        return new VPLNode(method);
    }

    private ClickListener menuListener = new ClickListener(Input.Buttons.RIGHT) {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Vector2 vec = localToStageCoordinates(pos.set(x, y).cpy());
            menu.show(getStage(), vec.x, vec.y);
        }
    };

}
