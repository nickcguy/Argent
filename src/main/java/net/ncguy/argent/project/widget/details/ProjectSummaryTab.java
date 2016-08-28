package net.ncguy.argent.project.widget.details;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.project.widget.ProjectDetails;

/**
 * Created by Guy on 27/08/2016.
 */
public class ProjectSummaryTab extends Tab {

    Table content;
    Table headerTable;
    Table entryTable;
    ScrollPane scrollPane;

    Cell nameCell;
    Cell typeCell;
    Cell sizeCell;
    Cell pathCell;

    public ProjectSummaryTab() {
        super(false, false);
        content = new Table(VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                entryTable.pack();
                float newY = getHeight() - (entryTable.getHeight() + headerTable.getHeight());
                if(newY < 0) newY = 0;
                scrollPane.setY(newY);
                super.act(delta);
            }
        };

        headerTable = new Table(VisUI.getSkin());

        entryTable = new Table(VisUI.getSkin());
        scrollPane = new ScrollPane(entryTable);

        scrollPane.setFlickScroll(true);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setOverscroll(false, false);

        content.add(headerTable).growX().row();
        content.add(scrollPane).grow().row();

        nameCell = headerTable.add("Name").width(300);
        typeCell = headerTable.add("Type").width(128);
        sizeCell = headerTable.add("Size").width(128);
        pathCell = headerTable.add("Path").growX();


        reset();
    }

    public void reset() {
        entryTable.clearChildren();
    }

    public void addAssetEntry(ProjectDetails.AssetEntry entry) {
        entryTable.add(entry.name).width(nameCell.getActorWidth());
        entryTable.add(entry.type).width(typeCell.getActorWidth());
        entryTable.add(entry.size).width(sizeCell.getActorWidth());
        entryTable.add(entry.path).width(pathCell.getActorWidth());
        entryTable.row();
    }

    @Override
    public String getTabTitle() {
        return "Overview";
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
