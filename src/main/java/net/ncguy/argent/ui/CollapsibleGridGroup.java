package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.Separator;

/**
 * Created by Guy on 01/08/2016.
 */
public class CollapsibleGridGroup extends Table {

    private String title;
    private TextButton collapseBtn;

    protected Table collapsibleContent;
    protected CollapseWidget collapsibleWidget;
    protected Label titleLabel;

    protected GridGroup gridGroup;

    public CollapsibleGridGroup(String title, GridGroup gridGroup) {
        super(VisUI.getSkin());
        collapsibleContent = new Table(VisUI.getSkin());
        titleLabel = new Label("", VisUI.getSkin());
        collapsibleWidget = new CollapseWidget(collapsibleContent);

        collapseBtn = new TextButton("^", VisUI.getSkin());
        collapseBtn.getLabel().setFontScale(.7f);

        this.gridGroup = gridGroup;
        collapsibleContent.add(this.gridGroup).expand().fill().row();

        setupUI();
        setupListeners();

        setTitle(title);
    }

    private void setupUI() {
        // Header
        final Table header = new Table(VisUI.getSkin());
        header.add(titleLabel);
        header.add(collapseBtn).right().top().width(20).height(20).expand().row();
        header.add(new Separator()).expandX().fillX().colspan(3).padBottom(4).row();

        add(header).expand().fill().row();
        add(collapsibleWidget).expand().fill().row();
    }
    private void setupListeners() {
        collapseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapse(!isCollapsed());
            }
        });
    }

    public void setTitle(String title) {
        titleLabel.setText(this.title = title);
    }

    public String getTitle() {
        return title;
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


}
