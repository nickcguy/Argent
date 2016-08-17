package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.widgets.sidebar.DebugTab;
import net.ncguy.argent.editor.widgets.sidebar.MaterialTab;
import net.ncguy.argent.editor.widgets.sidebar.OutlineTab;

/**
 * Created by Guy on 27/07/2016.
 */
public class Sidebar extends Table implements TabbedPaneListener {

    private float width = 300;

    private TabbedPane tabbedPane;
    private Table contentContainer;

    private OutlineTab outline;
    private MaterialTab materials;
    private DebugTab debug;

    private EditorUI editorUI;

    public Sidebar(EditorUI editorUI) {
        super(VisUI.getSkin());
        this.editorUI = editorUI;
        TabbedPane.TabbedPaneStyle style = VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class);
        style.vertical = false;
        tabbedPane = new TabbedPane(style);
        outline = new OutlineTab(editorUI);
        materials = new MaterialTab();
        debug = new DebugTab(editorUI);

        setupUI();

        tabbedPane.switchTab(outline);
        switchedTab(outline);
        tabbedPane.addListener(this);
    }

    public void setupUI() {
        contentContainer = new Table(VisUI.getSkin());
        contentContainer.setBackground(VisUI.getSkin().getDrawable("default-pane"));
        contentContainer.align(Align.topLeft);

        tabbedPane.add(outline);
        tabbedPane.add(materials);
        tabbedPane.add(debug);

        add(tabbedPane.getTable()).width(width).top().left().row();
        add(contentContainer).width(width).top().left().expandY().fillY().row();
    }

    @Override
    public void switchedTab(Tab tab) {
        contentContainer.clear();
        contentContainer.add(tab.getContentTable()).fill().expand();
    }

    @Override public void removedTab(Tab tab) { /* NOOP */ }
    @Override public void removedAllTabs() { /* NOOP */ }
}
