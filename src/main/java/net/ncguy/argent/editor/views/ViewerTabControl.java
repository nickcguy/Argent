package net.ncguy.argent.editor.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.vpl.VPLViewer;

/**
 * Created by Guy on 02/08/2016.
 */
public class ViewerTabControl extends Table implements TabbedPaneListener {

    private EditorUI editorUI;

    private SceneViewer sceneViewer;
    private MaterialViewer materialViewer;
    private VPLViewer vplViewer;

    private TabbedPane tabPane;
    private Table tabContent;
    private Tab activeTab;

    public ViewerTabControl(EditorUI editorUI) {
        sceneViewer = new SceneViewer(editorUI);
        materialViewer = new MaterialViewer(editorUI);
        vplViewer = new VPLViewer(editorUI);
        this.editorUI = editorUI;
        tabContent = new Table(VisUI.getSkin());
        tabPane = new TabbedPane();

        tabPane.addListener(this);

        tabPane.add(sceneViewer);
        tabPane.add(materialViewer);
        tabPane.add(vplViewer);

        tabPane.switchTab(sceneViewer);

        add(tabPane.getTable()).expandX().fillX().row();
        add(tabContent).expand().fill().row();
    }

    @Override
    public void switchedTab(Tab tab) {
        if(tab == activeTab) return;
        if(activeTab instanceof ViewTab)
            ((ViewTab) activeTab).onClose();

        tabContent.clearChildren();
        activeTab = tab;
        if(tab != null) {
            tabContent.add(tab.getContentTable()).expand().fill().row();
            if(tab instanceof ViewTab)
                ((ViewTab) tab).onOpen();
        }
    }

    @Override
    public void removedTab(Tab tab) {
        // NOOP
    }

    @Override
    public void removedAllTabs() {
        // NOOP
    }

    public SceneViewer getSceneViewer() {
        return sceneViewer;
    }
}
