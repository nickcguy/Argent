package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.widgets.component.BaseInspectorWidget;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.IdentifierWidget;
import net.ncguy.argent.editor.widgets.component.TransformWidget;
import net.ncguy.argent.entity.ComponentSet;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.event.WorldEntityComponentChangeEvent;
import net.ncguy.argent.event.WorldEntityModifiedEvent;
import net.ncguy.argent.event.WorldEntitySelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by Guy on 27/07/2016.
 */
public class Inspector extends Table implements WorldEntitySelectedEvent.WorldEntitySelectedListener, WorldEntityModifiedEvent.WorldEntityModifiedListener, WorldEntityComponentChangeEvent.WorldEntityComponentChangeListener {

    private Table root;
    private ScrollPane scroller;

    private IdentifierWidget identifierWidget;
    private TransformWidget transformWidget;
    private Array<ComponentWidget> componentWidgets;

    private TextButton addComponentBtn;
    private Table componentTable;
    private SearchableList<Class<? extends ArgentComponent>> componentSelection;

    @Inject
    private ProjectManager projectManager;
    @Inject
    private ComponentSet availableComponents;
    private EditorUI editorUI;

    public Inspector(EditorUI editorUI) {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        Argent.event.register(this);

        this.editorUI = editorUI;

        identifierWidget = new IdentifierWidget();
        transformWidget = new TransformWidget();
        componentWidgets = new Array<>();
        addComponentBtn = new TextButton("Add Component", VisUI.getSkin());
        componentTable = new Table(VisUI.getSkin());
        componentSelection = new SearchableList<>();

        init();
        setupUI();
        setupListeners();
    }

    private Set<Class<? extends ArgentComponent>> availableComponents() {
        if(availableComponents == null) {
            ArgentInjector.inject(this);
        }
        return availableComponents.set();
    }

    public void init() {
        setBackground("default-pane");
        add("Inspector").expandX().fillX().pad(3).row();
        add(new Separator()).expandX().fillX().row();
        root = new Table(VisUI.getSkin());
        root.align(Align.top);
        scroller = new ScrollPane(root, VisUI.getSkin());
        scroller.setScrollingDisabled(true, false);
        scroller.setFlickScroll(false);
        scroller.setFadeScrollBars(false);
        scroller.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                editorUI.setScrollFocus(scroller);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                editorUI.setScrollFocus(null);
            }
        });
        add(scroller).expand().fill().top();
    }
    public void setupUI() {
        root.add(identifierWidget).expandX().fillX().pad(7).row();
        root.add(transformWidget).expandX().fillX().pad(7).row();
        componentWidgets.forEach(w -> componentTable.add(w).row());
        root.add(componentTable).expandX().fillX().pad(7).row();
        root.add(addComponentBtn).expandX().fill().top().center().pad(10).row();

//        availableComponents
//        componentSelection
        componentSelection.setChangeListener(itemCls -> {
            WorldEntity e = projectManager.current().currScene.selected();
            if(e == null) return;
            try {
                Constructor cons = itemCls.value.getConstructor(WorldEntity.class);
                if(cons == null) return;
                Object obj = cons.newInstance(e);
                if(obj instanceof ArgentComponent)
                    e.add((ArgentComponent)obj);
                Argent.event.post(new WorldEntityComponentChangeEvent(WorldEntityComponentChangeEvent.ChangeType.ADD));
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        });
    }
    public void populateAvailableComponents() {
        componentTable.clearChildren();
        buildComponentWidgets();
        componentWidgets.forEach(w -> componentTable.add(w).row());

        componentSelection.clearItems();
        WorldEntity e = projectManager.current().currScene.selected();
        if(e == null) return;
        availableComponents().stream().filter(e::canApply).forEach(cls -> {
            componentSelection.addItem(new SearchableList.Item<>(null, StringUtils.splitCamelCase(cls.getSimpleName()), cls));
        });
    }
    public void setupListeners() {
        addComponentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                componentSelection.show(editorUI, addComponentBtn.localToStageCoordinates(new Vector2(x, y)));
            }
        });
    }

    private void buildComponentWidgets() {
        final ProjectContext context = projectManager.current();
        componentWidgets.clear();
        if(context.currScene.selected() != null) {
            for(ArgentComponent component : context.currScene.selected().components()) {
                ComponentWidget widget = createWidget(component);
                if(widget != null) {
                    widget.component = component;
                    componentWidgets.add(widget);
                }
            }
        }
    }
    private ComponentWidget createWidget(ArgentComponent component) {
        return createWidget(component.widgetClass(), component);
    }
    private ComponentWidget createWidget(Class<? extends ComponentWidget> cls, ArgentComponent component) {
        if(cls == null) return null;
        try {
            Object obj = cls.getConstructor(component.getClass()).newInstance(component);
            if(obj instanceof BaseInspectorWidget)
                return (ComponentWidget)obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setValues(WorldEntity we) {
        final ProjectContext context = projectManager.current();
        identifierWidget.setValues(we);
        transformWidget.setValues(we);
        buildComponentWidgets();
        componentTable.clearChildren();
        componentWidgets.forEach(c -> {
            componentTable.add(c).expand().fill().row();
            c.setValues(we);
        });
    }

    @Override
    public void onWorldEntitySelected(WorldEntitySelectedEvent event) {
        populateAvailableComponents();
        setValues(projectManager.current().currScene.selected());
    }

    @Override
    public void onWorldEntityModified(WorldEntityModifiedEvent event) {
        WorldEntity we = projectManager.current().currScene.selected();
        identifierWidget.setValues(we);
        transformWidget.setValues(we);
    }

    @Override
    public void onWorldEntityComponentChange(WorldEntityComponentChangeEvent event) {
        populateAvailableComponents();
    }
}
