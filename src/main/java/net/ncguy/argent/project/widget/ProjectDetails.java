package net.ncguy.argent.project.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import net.ncguy.argent.Argent;
import net.ncguy.argent.concurrent.AtomicFloat;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.project.MetaSelectedEvent;
import net.ncguy.argent.project.ProjectMeta;
import net.ncguy.argent.project.widget.details.ProjectDetailsStatusBar;
import net.ncguy.argent.project.widget.details.ProjectSummaryTab;
import net.ncguy.argent.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Guy on 26/08/2016.
 */
public class ProjectDetails extends Table implements TabbedPaneListener, MetaSelectedEvent.MetaSelectedListener {

    AtomicFloat traversalProgress;

    TabbedPane tabControl;
    Table activeContent;
    ProjectDetailsStatusBar statusBar;
    Thread crawlerThread;

    TextButton openProjectBtn;
    ProjectMeta selectedMeta;

    ProjectSummaryTab summaryTab;
    private ProjectManager projectManager;

    public ProjectDetails(ProjectManager projectManager) {
        super(VisUI.getSkin());
        this.projectManager = projectManager;
        Argent.event.register(this);
        setBackground("NearBlack");
        traversalProgress = new AtomicFloat();

        tabControl = new TabbedPane();
        tabControl.addListener(this);
        activeContent = new Table(VisUI.getSkin());
        statusBar = new ProjectDetailsStatusBar();

        summaryTab = new ProjectSummaryTab();

        tabControl.add(summaryTab);
        tabControl.switchTab(summaryTab);

        openProjectBtn = new TextButton("Open Project", VisUI.getSkin());
        openProjectBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                projectManager.selectContext(projectManager.loadContext(selectedMeta));
            }
        });

        add(openProjectBtn).growX().row();

        add(tabControl.getTable()).growX().row();
        add(activeContent).grow().row();
        add(statusBar).growX().left().row();
    }

    @Override
    public void act(float delta) {
        statusBar.bar.setValue(traversalProgress.floatValue() * 100);
        openProjectBtn.setDisabled(selectedMeta == null);
        openProjectBtn.toFront();
        super.act(delta);
    }

    public void loadDetails(ProjectMeta meta) {
        selectedMeta = meta;
        summaryTab.reset();
        if(crawlerThread != null) {
            crawlerThread.interrupt();
            crawlerThread = null;
        }
        if(meta == null) return;
        crawlerThread = new Thread(() -> {
            statusBar.bar.setVisible(true);
            File root = new File(meta.path);
            Path path = root.toPath();
            int count = FileUtils.getFileDescendantCount(root);
            if(count <= 0) count = 1;
            final int[] index = {0};
            try {
                int finalCount = count;
                FileUtils.traverseFileTree(path, step -> {
                    index[0]++;
                    addFile(step.path);
                    float prg = (float)index[0] / (float)finalCount;
                    traversalProgress.set(prg);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Crawler Thread");
        crawlerThread.start();
    }

    public void addFile(Path path) {
        AssetEntry entry = new AssetEntry();
        File file = path.toFile();
        entry.name = file.getName();
        entry.path = file.getAbsolutePath();
        entry.type = FileUtils.getFileExtension(file);
        entry.size = FileUtils.getFileSizeString(file);
        Gdx.app.postRunnable(() -> addAssetEntry(entry));
    }

    public void addAssetEntry(AssetEntry entry) {
        summaryTab.addAssetEntry(entry);
    }

    @Override
    public void switchedTab(Tab tab) {
        activeContent.clearChildren();
        if(tab == null) return;
        Table table = tab.getContentTable();
        if(table == null) return;
        activeContent.add(table).grow();
    }

    @Override
    public void removedTab(Tab tab) {
        // NOOP
    }

    @Override
    public void removedAllTabs() {
        // NOOP
    }

    @Override
    public void onMetaSelected(MetaSelectedEvent event) {
        loadDetails(event.meta);
    }

    public static class AssetEntry {
        public String path;
        public String name;
        public String type;
        public String size;
    }

}
