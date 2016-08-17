package net.ncguy.argent.editorOld.panels;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.tree.TreeObjectWrapper;
import net.ncguy.argent.data.tree.TreePopulator;
import net.ncguy.argent.data.tree.VisitableTree;
import net.ncguy.argent.data.tree.sample.PrintIndentedVisitor;
import net.ncguy.argent.editor.EditorRoot;
import net.ncguy.argent.editor.builders.GdxSetBuilderVisitor;
import net.ncguy.argent.editorOld.controller.ControllerType;
import net.ncguy.argent.editorOld.controller.PreviewCameraController;
import net.ncguy.argent.editorOld.panels.menu.ObjectContextMenu;
import net.ncguy.argent.entity.EntityMappers;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.factory.ArgentComponentFactory;
import net.ncguy.argent.ui.ArgentList;
import net.ncguy.argent.ui.Icons;
import net.ncguy.argent.ui.LabeledActor;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.utils.TextureCache;

import java.util.ArrayList;

/**
 * Created by Guy on 18/07/2016.
 */
public class ObjectDataPanel<T extends WorldEntity> extends AbstractEditorPanel<T> {

    private Image bgImage;
    private Image previewImage;
    private SplitPane previewListSplit, dataConfigSplit, configComponentSplit, componentMetaSplit;
    private ScrollPane configGrp, componentGrp, componentMetaGrp;
    private Tree configTree, componentTree;
    private Group componentMetaGroup;
    private ArgentList<T> objList;
    private PreviewCameraController previewCameraController;
    private TextureRegion previewReg;
    private TextButton resetCameraBtn;
    private Group previewImageGroup;
    private SelectBox<ControllerType> controllerBehaviourBox;
    private ControllerType controllerBehaviour = ControllerType.NONE;
    private Table diagTable, metaTable;
    private Label fpsCounter;
    private Label deltaCounter;
    private WindowedMean deltaBuffer = new WindowedMean(30);
    private ImageTextButton addComponentBtn, removeComponentBtn;
    private Label metaLabel;
    private ScrollPane metaLabelScroller;
    private List<String> componentErrorList;

    protected ObjectContextMenu<T> contextMenu;
    private EventListener componentTree_ChangeListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            Tree.Node node = componentTree.getSelection().first();
            if(node != null) {
                Object obj = node.getObject();
                if(obj instanceof ArgentComponentFactory)
                    populateComponentMeta((ArgentComponentFactory)obj);
            }
        }
    };

    public ObjectDataPanel(EditorRoot<T> editorRoot) {
        super(editorRoot, "");
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name) {
        super(editorRoot, name);
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name, boolean savable) {
        super(editorRoot, name, savable);
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name, boolean savable, boolean closeableByUser) {
        super(editorRoot, name, savable, closeableByUser);
    }

    @Override
    protected void init() {
        this.name = "Object Data";
        this.group = new Group();
        this.configTree = new Tree(VisUI.getSkin());
        this.configGrp = new ScrollPane(this.configTree);
        this.configGrp.setScrollingDisabled(true, false);
        this.componentTree = new Tree(VisUI.getSkin());
        this.componentGrp = new ScrollPane(this.componentTree);
        this.componentGrp.setScrollingDisabled(true, false);
        this.componentMetaGroup = new Group();
        this.addComponentBtn = new ImageTextButton("Add Component", VisUI.getSkin());
        this.removeComponentBtn = new ImageTextButton("Remove Component", VisUI.getSkin());
        this.metaLabel = new Label("", VisUI.getSkin());
        this.metaLabel.setWrap(true);
        this.metaLabel.setFillParent(true);
        this.metaLabelScroller = new ScrollPane(this.metaLabel);
//        this.metaLabelScroller.setFillParent(true);
        this.metaTable = new Table(VisUI.getSkin());
        this.addComponentBtn.getImage().setDrawable(Icons.Icon.IMPORT.drawable());
        this.removeComponentBtn.getImage().setDrawable(Icons.Icon.EXPORT.drawable());

        this.metaTable.add(this.metaLabelScroller).colspan(2).width(192).row();
        this.metaTable.add(new Separator()).colspan(2).row();
        this.metaTable.add("Errors");
        this.metaTable.add(componentErrorList = new List<>(VisUI.getSkin())).row();
        this.metaTable.add(new Separator()).colspan(2).row();
        this.metaTable.add(this.addComponentBtn, this.removeComponentBtn);
        this.addComponentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArgentComponentFactory fac = getSelectedFactory();
                if(fac != null) addComponent(fac);
            }
        });
        this.removeComponentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArgentComponentFactory fac = getSelectedFactory();
                if(fac != null) removeComponent(fac);
            }
        });
        this.componentMetaGroup.addActor(this.metaTable);

        this.componentMetaGrp = new ScrollPane(this.componentMetaGroup);
        this.objList = new ArgentList<>(VisUI.getSkin());
        this.objList.onRightClick(() -> {
            this.contextMenu.target(null);
            this.contextMenu.show();
        });
        this.resetCameraBtn = new TextButton("Reset Camera", VisUI.getSkin());
        this.resetCameraBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewCameraController.reset();
            }
        });

        this.bgImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureCache.pixel())));
        this.bgImage.setColor(.32f, .32f, .32f, 1);
        this.previewImage = new Image(TextureCache.pixel());
        this.previewImageGroup = new Group();
        this.previewImageGroup.addActor(this.previewImage);
        this.previewImage.setFillParent(true);
        this.previewImageGroup.addActor(this.resetCameraBtn);
        this.controllerBehaviourBox = new SelectBox<>(VisUI.getSkin());
        this.controllerBehaviourBox.setItems(ControllerType.values());
        this.controllerBehaviourBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controllerBehaviour = controllerBehaviourBox.getSelected();
            }
        });
        this.previewImageGroup.addActor(this.controllerBehaviourBox);

        this.group.addActor(this.bgImage);
        this.previewListSplit = new SplitPane(this.previewImageGroup, this.objList, true, VisUI.getSkin()){
            @Override
            public void layout() {
                super.layout();
                previewList_Layout();
            }
        };

        this.componentMetaSplit = new SplitPane(this.componentGrp, this.componentMetaGrp, false, VisUI.getSkin()) {
            @Override
            public void layout() {
                super.layout();
                componentMeta_Layout();
            }
        };
        this.configComponentSplit = new SplitPane(this.configGrp, this.componentMetaSplit, true, VisUI.getSkin()) {
            @Override
            public void layout() {
                super.layout();
                configComponent_Layout();
            }
        };
        this.dataConfigSplit = new SplitPane(this.previewListSplit, this.configComponentSplit, false, VisUI.getSkin()){
            @Override
            public void layout() {
                super.layout();
                dataConfig_Layout();
            }
        };
        this.group.addActor(this.dataConfigSplit);

        this.diagTable = new Table(VisUI.getSkin());
        this.diagTable.add("FPS").width(64);
        this.diagTable.add(this.fpsCounter = new Label("...", VisUI.getSkin())).width(128).row();
        this.diagTable.add("Delta").width(64);
        this.diagTable.add(this.deltaCounter = new Label("...", VisUI.getSkin())).width(128).row();
        this.previewImageGroup.addActor(this.diagTable);

        this.contextMenu = new ObjectContextMenu<>(editorRoot);

        this.previewCameraController = new PreviewCameraController(editorRoot.cameraSupplier().get());
    }

    public T getSelected() {
        return editorRoot.selected();
    }

    public ArgentComponentFactory<?> getSelectedFactory() {
        Tree.Node node = componentTree.getSelection().first();
        if(node == null) return null;
        if(node.getObject() instanceof ArgentComponentFactory)
            return (ArgentComponentFactory<?>) node.getObject();
        return null;
    }

    public void reloadAllLists(T entity) {
        selectObj(entity);
    }

    public void addComponent(ArgentComponentFactory<?> factory) {
        T entity = getSelected();
        if(entity == null) return;
        Component c = entity.getComponent(factory.componentClass());
        if(c != null) return;
        entity.add(factory.build(entity));
        reloadAllLists(entity);
    }
    public void removeComponent(ArgentComponentFactory<?> factory) {
        T entity = getSelected();
        if(entity == null) return;
        Component c = entity.getComponent(factory.componentClass());
        if(c == null) return;
        entity.remove(factory.componentClass());
        reloadAllLists(entity);
    }

    public void populateComponentList(T obj) {
        componentTree.clearChildren();
        componentTree.removeListener(componentTree_ChangeListener);
        if(obj == null) return;
        Tree.Node rootNode = new Tree.Node(new Label("Components", VisUI.getSkin()));
        componentTree.add(rootNode);
        editorRoot.select(obj);
        EditorRoot.componentFactoryList().forEach(factory -> {
            Tree.Node node = new Tree.Node(new Label(factory.name(), VisUI.getSkin()));
            if(factory.canBeApplied(obj)) {
                node.setIcon(Icons.Icon.UNLOCKED.drawable());
                node.getActor().getColor().set(Color.WHITE);
            }else{
                node.setIcon(Icons.Icon.LOCKED.drawable());
                node.getActor().getColor().set(ColorUtils.HSVtoRGB(0, 80, 100));
            }
            node.setObject(factory);
            rootNode.add(node);
        });
        rootNode.setExpanded(true);
        componentTree.addListener(componentTree_ChangeListener);
    }
    public void populateComponentMeta(ArgentComponentFactory<?> fac) {
        this.metaLabel.setText(fac.meta());
        this.componentErrorList.clearItems();
        ArrayList<String> errors = fac.errors(editorRoot.selected());
        String[] errs = new String[errors.size()];
        errors.toArray(errs);
        componentErrorList.setItems(errs);
//        fac.errors(editorRoot.selected()).forEach(componentErrorList.getItems()::addZone);
        this.addComponentBtn.setDisabled(!fac.canBeApplied(editorRoot.selected()));
        this.removeComponentBtn.setDisabled(!fac.canBeRemoved(editorRoot.selected()));
    }

    public void layout() {
        previewList_Layout();
        dataConfig_Layout();
        componentMeta_Layout();
        configComponent_Layout();
    }

    public void previewList_Layout() {
        this.resetCameraBtn.pack();
        this.resetCameraBtn.setPosition(this.previewImageGroup.getWidth()-2, this.previewImageGroup.getHeight()-2, Align.topRight);
        this.controllerBehaviourBox.pack();
        this.controllerBehaviourBox.setPosition(this.previewImageGroup.getWidth()-2, this.resetCameraBtn.getY()-2, Align.topRight);
        this.diagTable.pack();
        this.diagTable.setPosition((this.diagTable.getWidth()/2)+2, this.previewImageGroup.getHeight()-2, Align.top);

        if(this.editorRoot.cached())
            editorRoot.resizeCamera(this.previewImageGroup.getWidth(), this.previewImageGroup.getHeight());
    }
    public void dataConfig_Layout() {
        invalidateConfigList();
    }
    public void componentMeta_Layout() {
        this.metaTable.setFillParent(true);
    }
    public void configComponent_Layout() {

    }

    public void invalidateConfigList() {
        this.configTree.setWidth(this.configGrp.getWidth());
        getAllNodes(this.configTree).stream().filter(node -> node.getActor() instanceof LabeledActor).forEach(node -> {
            LabeledActor nodeActor = (LabeledActor) node.getActor();
            float labelWidth = 256;
            float compPadding = 50;
            float compWidth = this.configTree.getWidth() - labelWidth;
            nodeActor.setWidth(compWidth + compPadding + labelWidth);
            nodeActor.labelCell().width(labelWidth);
            nodeActor.actorCell().width(compWidth - compPadding);
        });
        this.configTree.invalidate();
    }

    public ArrayList<Tree.Node> getAllNodes(Tree configTree) {
        ArrayList<Tree.Node> nodeList = new ArrayList<>();
        configTree.getNodes().forEach(n -> {
            nodeList.add(n);
            getAllNodes(nodeList, n);
        });
        return nodeList;
    }
    public void getAllNodes(final ArrayList<Tree.Node> nodeList, final Tree.Node node) {
        node.getChildren().forEach(n -> {
            nodeList.add(n);
            getAllNodes(nodeList, n);
        });
    }

    public void populateObjList() {
        objList.clearItems();
        editorRoot.world().instances().forEach(i -> objList.addItem(i, this::selectObj, this::onObjRightClick));
        objList.select(editorRoot.selected());
    }

    public void onObjRightClick(ArgentList.ArgentListElement<T> element) {
        this.contextMenu.show(element);
    }

    public void selectObj(T obj) {
        configTree.clearChildren();
        populateComponentList(obj);
        editorRoot.select(null);
        java.util.List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        obj.getConfigurableAttributes(attrs);
        VisitableTree<TreeObjectWrapper<ConfigurableAttribute<?>>> attrTree = new VisitableTree<>(new TreeObjectWrapper<>("Root"));
        TreePopulator.populate(attrTree, attrs, "\\|",
                (d) -> d.category()+"|"+d.displayName(),
                (d, s) -> {
                    if(s.equals(d.category()+"|"+d.displayName())) return new TreeObjectWrapper<>(d, d.displayName());
                    String l = s;
                    if(l.contains("|"))
                        l = l.substring(l.lastIndexOf('|')+1);
                    return new TreeObjectWrapper<>(l);
                });
        Tree.Node rootNode = new Tree.Node(new Label("Root", VisUI.getSkin()));
        this.configTree.add(rootNode);
        attrTree.accept(new PrintIndentedVisitor<>(0));
        attrTree.accept(new GdxSetBuilderVisitor(configTree, rootNode));
        configTree.getRootNodes().forEach(n -> n.setExpanded(true));
        editorRoot.select(obj);
    }

    @Override
    public void onSwitchTo() {
        editorRoot.cacheCamera();
        previewReg = new TextureRegion(editorRoot.getWrappedView());
        previewReg.setV(1);
        previewReg.setV2(0);
        this.previewImage.setDrawable(new TextureRegionDrawable(previewReg));
        AppUtils.Input.addInputProcessor(this.previewCameraController);
        populateObjList();
        layout();
    }

    @Override
    public void onSwitchFrom() {
        AppUtils.Input.removeInputProcessor(this.previewCameraController);
        editorRoot.revertCamera();
    }

    @Override
    public void resize(int w, int h) {
        this.bgImage.setBounds(0, 0, w, h);
        this.dataConfigSplit.setBounds(0, 0, w, h);
//        this.previewImage.setBounds(w-512, h*0.9f, 384, -(h*0.8f));
        layout();
    }

    public void updateDiag(float delta) {
        this.fpsCounter.setText(Gdx.graphics.getFramesPerSecond()+"");
        deltaBuffer.addValue(delta);
        delta = deltaBuffer.getMean() * 1000;
        String d = delta+"";
        if(d.contains("."))
            d = d.substring(0, d.indexOf('.'));
        this.deltaCounter.setText(d+"ms");
    }

    @Override
    public void act(float delta) {
//        this.previewCameraController.update();
        previewReg.setTexture(editorRoot.getWrappedView());
        this.bgImage.toBack();
        Vector3 point = new Vector3();
        T sel = this.editorRoot.selected();
        if(sel != null) {
            if (EntityMappers.transformMapper.has(sel))
                EntityMappers.transformMapper.get(sel).transform.getTranslation(point);
        }
        this.controllerBehaviour.act((PerspectiveCamera) this.editorRoot.cameraSupplier().get(), point);
//        this.previewCameraController.focusOn(point);
//        AppUtils.Graphics.orbit((PerspectiveCamera) this.editorRoot.cameraSupplier().get(), point);
//        this.editorRoot.cameraSupplier().get();
        updateDiag(delta);
    }
    FrameBuffer test = new FrameBuffer(Pixmap.Format.RGBA8888, 1600, 900, true);
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        test.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        batch.draw(editorRoot.getWrappedView(), 0, 0, 1600, 900);
        if(Gdx.input.isKeyJustPressed(Input.Keys.F4))
            ScreenshotUtils.saveScreenshot(1600, 900, "Editor");
        test.end();
    }

    @Override
    public boolean blockMainRender() {
        return false;
    }

    @Override
    public boolean wrapMainRender() {
        return true;
    }
}

