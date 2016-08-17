package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Guy on 11/08/2016.
 */
public class ArgentTabbedPane extends Table implements TabbedPaneListener {

    PopupMenu tabSelection;
    ImageButton selectionBtn;
    Table headerTable;
    Table contentTable;
    ScrollPane contentScroller;
    Array<Tab> tabs;

    private Tab activeTab;
    public boolean autoUpdate = true;

    public BiConsumer<Tab, Tab> onSwitchTab;
    public Consumer<Tab> onRemove;
    public Runnable onRemoveAll;

    @Inject
    EditorUI editorUI;

    public ArgentTabbedPane() {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        tabSelection = new PopupMenu();
        selectionBtn = new ImageButton(VisUI.getSkin());
        headerTable = new Table(VisUI.getSkin());
        contentTable = new Table(VisUI.getSkin());
        contentScroller = new ScrollPane(contentTable);
        tabs = new Array<>();

        Image i = new Image(Icons.Icon.SETTINGS_VIEW.drawable());
        i.setSize(32, 32);
        selectionBtn.addActor(i);

        selectionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Stage stage = selectionBtn.getStage();
                if(stage == null) {
                    editorUI.getToaster().info("No stage found for selection");
                    return;
                }
                Vector2 pos = selectionBtn.localToStageCoordinates(new Vector2(x, y));
                tabSelection.showMenu(stage, pos.x, pos.y);
            }
        });

        add(selectionBtn).left().top().size(32).padRight(4);
        add(headerTable).expandX().fillX().top().left().row();
        add(contentScroller).colspan(2).expand().fill().row();
    }

    public void invalidateSelect() {
        tabSelection.clearChildren();
        tabs.forEach(tab -> tabSelection.addItem(new MenuItem(tab.getTabTitle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switchedTab(tab);
            }
        })));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        setDebug(false, true);
    }

    public Array<Tab> getTabs() {
        return tabs;
    }

    public void addTab(Tab tab) {
        addTab(tab, autoUpdate);
    }
    public void addTab(Tab tab, boolean auto) {
        tabs.add(tab);
        if(auto) invalidateSelect();
    }

    public void removeSelected() {
        removeSelected(autoUpdate);
    }
    public void removeTab(Tab tab) {
        removeTab(tab, autoUpdate);
    }

    public void removeSelected(boolean auto) {
        removeTab(activeTab, auto);
    }
    public void removeTab(Tab tab, boolean auto) {
        tabs.removeValue(tab, false);
        if(auto) invalidateSelect();
    }

    public void switchTo(Tab tab) {
        if(tab.equals(activeTab)) return;
        if(onSwitchTab != null) onSwitchTab.accept(activeTab, tab);
        switchedTab(activeTab = tab);
    }
    public void remove(Tab tab) {
        if(onRemove != null) onRemove.accept(tab);
        removedTab(tab);
    }
    public void removeAll() {
        if(onRemoveAll != null) onRemoveAll.run();
        removedAllTabs();
    }

    @Override
    public void switchedTab(Tab tab) {
        contentTable.clearChildren();
        headerTable.clearChildren();
        if(tab == null) return;
        headerTable.add(tab.getTabTitle()).expandX().fillX().row();
        contentTable.add(tab.getContentTable()).expand().fill().row();
    }

    @Override
    public void removedTab(Tab tab) {
        if(activeTab == tab)
            contentTable.clear();
    }

    @Override
    public void removedAllTabs() {
        autoUpdate = false;
        tabs.forEach(this::removeTab);
        tabs.clear();
        autoUpdate = true;
        invalidateSelect();
    }
}
