package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import net.ncguy.argent.Argent;
import net.ncguy.argent.event.StringPacketEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.vpl.compiler.VPLCompiler;
import net.ncguy.argent.vpl.nodes.factory.NodeFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLGraph extends Group {

    public List<VPLNode> nodes;
    List<Method> nodeMethods;
    public String[] tags;
    VPLContextMenu menu;
    Vector2 pos = new Vector2();
    public Rectangle bounds;
    public Selection<VPLNode> nodeSelection;
    public boolean draggingPin = false;
    public VPLNodeContextMenu nodeContextMenu;

    public VPLCompiler compiler;

    public VPLGraph(String... tags) {
        ArgentInjector.inject(this);
        this.nodeContextMenu = new VPLNodeContextMenu(this);
        this.nodes = new ArrayList<>();
        this.menu = new VPLContextMenu(this);
        this.menu.setChangeListener(item -> {
            Object obj = item.value;
            if(obj instanceof Method)
                addNode((Method)obj);
            else if(obj instanceof NodeFactory)
                addNode(((NodeFactory)obj).construct(this));
        });

        refreshMenu(tags);

        addListener(menuListener);
        nodeSelection = new Selection<>();
        nodeSelection.setMultiple(true);
        attachListener();
    }

    public void setTags(String... tags) {
        this.tags = tags;
    }
    public void refreshMenu(String... tags) {
        setTags(tags);
        refreshMenu();
    }
    public void refreshMenu() {

        this.nodeMethods = VPLManager.instance().getNodesWithTags(this.tags);
        this.menu.clearItems();

        List<Object> objs = new ArrayList<>();

//        this.menu.setMethods(this.nodeMethods);

//        VPLManager.instance().registeredNodeFactories.forEach(this::addFactoryItem);

        objs.addAll(this.nodeMethods);
        objs.addAll(VPLManager.instance().registeredNodeFactories.stream()
                .filter(fac -> fac.hasTag(this.tags)).collect(Collectors.toList()));

        objs.stream().sorted(this::sort).distinct().forEach(this::allocate);
    }
    private int sort(Object obj1, Object obj2) {
        String s1 = getName(obj1);
        String s2 = getName(obj2);
        return Argent.lexicographicalSorter.compare(s1, s2);
    }
    private String getName(Object obj) {
        if(obj instanceof Method)
            VPLManager.instance().getDisplayName((Method) obj);
        return obj.toString();
    }
    private void allocate(Object obj) {
        if(obj instanceof Method)
            addMethodItem((Method)obj);
        else if(obj instanceof NodeFactory) {
            addFactoryItem((NodeFactory)obj);
        }
    }
    private void addMethodItem(Method m) {
        SearchableList.Item<Object> item = new SearchableList.Item<>(null, VPLManager.instance().getDisplayName(m), m, VPLManager.instance().getMatchable(m));
        this.menu.addItem(item);
    }
    private void addFactoryItem(NodeFactory factory) {
        SearchableList.Item<Object> item =
                new SearchableList.Item<>(null, factory.toString(), factory, factory.getMatchable());
        this.menu.addItem(item);
    }

    private void attachListener() {
        InputListener listener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nodeSelection.clear();
                return super.touchDown(event, x, y, pointer, button);
            }
        };
        addListener(listener);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        VPLNodeRenderable.instance().renderSplines(batch, this);
    }

    public void updateNodes() {
        clearChildren();
        this.nodes.forEach(node -> {
            addActor(node);
            node.setPosition(node.position.x, node.position.y);
        });
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
        return new VPLNode(this, method);
    }

    public boolean isNodeSelected(VPLNode node) {
        return nodeSelection.contains(node);
    }

    public void moveSelectedBy() {
        moveSelectedBy(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
    }
    public void moveSelectedBy(float x, float y) {
        nodeSelection.forEach(node -> moveNodeBy(node, x, y));
    }

    public void moveNodeBy(VPLNode node, float x, float y) {
        if(!draggingPin)
            node.moveBy(x, y);
    }

    private ClickListener menuListener = new ClickListener(Input.Buttons.RIGHT) {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Vector2 vec = localToStageCoordinates(pos.set(x, y).cpy());
            menu.show(getStage(), vec.x, vec.y);
        }
    };

    public void removeNode(VPLNode<?> node) {
        node.pinSet.forEach(VPLPin::disconnectAll);
        node.remove();
        this.nodes.remove(node);
    }

    public Set<VPLNode<?>> getNetworkedNodes(VPLNode<?> node) {
        final Set<VPLNode<?>> nodes = node.getConnectedNodes();
        nodes.stream().collect(Collectors.toList()).forEach(n -> getNetworkedNodes(nodes, n));
        return nodes;
    }
    public void getNetworkedNodes(Set<VPLNode<?>> list, VPLNode<?> node) {
        node.getConnectedNodes(list);
    }

    public void removeAllNodes() {
        nodes.stream().collect(Collectors.toList()).forEach(this::removeNode);
    }

    public List<VPLPin> getAllPins() {
        List<VPLPin> set = new ArrayList<>();
        nodes.forEach(node -> set.addAll(node.pinSet));
        return set.stream().distinct().collect(Collectors.toList());
    }

    public List<VPLPin> getAllConnectedPins() {
        List<VPLPin> set = new ArrayList<>();
        nodes.forEach(node -> {
            Set<VPLPin> pin = node.pinSet;
            pin.stream().filter(VPLPin::isConnected).forEach(set::add);
        });
        return set.stream().distinct().collect(Collectors.toList());
    }


    protected StringPacketEvent toastPacket = new StringPacketEvent();

    public void info(String msg) {
        toastPacket.key = "toast|info";
        toastPacket.payload = msg;
        toastPacket.fire();
    }
    public void error(String msg) {
        toastPacket.key = "toast|error";
        toastPacket.payload = msg;
        toastPacket.fire();
    }
    public void success(String msg) {
        toastPacket.key = "toast|success";
        toastPacket.payload = msg;
        toastPacket.fire();
    }

}
