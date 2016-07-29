package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.WorldEntityComponentChangeEvent;
import net.ncguy.argent.event.WorldEntityModifiedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.CollapseWidget;

/**
 * Created by Guy on 27/07/2016.
 */
public abstract class BaseInspectorWidget extends Table {

    private String title;
    private TextButton collapseBtn;
    private TextButton deleteBtn;
    private Cell deletableBtnCell;

    protected Table collapsibleContent;
    protected CollapseWidget collapsibleWidget;
    protected Label titleLabel;

    protected boolean deletable;

    @Inject protected ProjectManager projectManager;
    @Inject protected CommandHistory commandHistory;

    public BaseInspectorWidget(String title) {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        collapsibleContent = new Table(VisUI.getSkin());
        titleLabel = new Label("", VisUI.getSkin());
        collapsibleWidget = new CollapseWidget(collapsibleContent);

        collapseBtn = new TextButton("^", VisUI.getSkin());
        collapseBtn.getLabel().setFontScale(.7f);

        deleteBtn = new TextButton("X", VisUI.getSkin());
        deleteBtn.getLabel().setFontScale(.7f);
        deleteBtn.getStyle().up = null;

        deletable = false;

        setupUI();
        setupListeners();

        setTitle(title);
    }

    private void setupUI() {
        // Header
        final Table header = new Table(VisUI.getSkin());
        deletableBtnCell = header.add(deleteBtn).top().left().padBottom(4);
        header.add(titleLabel);
        header.add(collapseBtn).right().top().width(20).height(20).expand().row();
        header.add(new Separator()).expandX().fillX().colspan(3).padBottom(4).row();

        add(header).expand().fill().row();
        add(collapsibleWidget).expand().fill().row();
        setDeletable(deletable);
    }
    private void setupListeners() {
        collapseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapse(!isCollapsed());
            }
        });
        deleteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delete();
            }
        });
    }

    public boolean isDeletable() { return deletable; }
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
        if(deletable) {
            deleteBtn.setVisible(true);
            deletableBtnCell.width(20).height(20).padRight(5);
        }else{
            deleteBtn.setVisible(false);
            deletableBtnCell.width(0).height(0).padRight(0);
        }
    }

    public boolean isCollapsed() { return collapsibleWidget.isCollapsed(); }

    public void collapse(boolean collapse) {
        collapsibleWidget.setCollapsed(collapse);
        if(collapse) {
            collapseBtn.setText("V");
        }else{
            collapseBtn.setText("^");
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        titleLabel.setText(title);
    }

    public Table getCollapsibleContent() {
        return collapsibleContent;
    }

    public void delete() {
        if(!isDeletable()) return;
        onDelete();
        Argent.event.post(new WorldEntityComponentChangeEvent(WorldEntityComponentChangeEvent.ChangeType.REMOVE));
        Argent.event.post(new WorldEntityModifiedEvent(projectManager.current().currScene.selected()));
    }

    public abstract void onDelete();
    public abstract void setValues(WorldEntity entity);


}
