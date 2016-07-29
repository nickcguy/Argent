package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.widgets.component.commands.NameCommand;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 27/07/2016.
 */
public class IdentifierWidget extends Table {

    private CheckBox active;
    private TextField name, tag;

    private WorldEntity entity;

    @Inject
    protected CommandHistory commandHistory;

    public IdentifierWidget() {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        active = new CheckBox("", VisUI.getSkin());
        active.setChecked(true);
        name = new TextField("Name", VisUI.getSkin());
        tag = new TextField("Untagged", VisUI.getSkin());
    }

    private void setupUI() {
        add(active).padBottom(4).left().top();
        add(name).padBottom(4).left().top().expandX().fillX().row();
        add("Tag: ").left().top();
        add(tag).left().top().expandX().fillX().row();
    }

    private void setupListeners() {
        name.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NameCommand cmd = new NameCommand(entity);
                cmd.setBefore(entity.name);
                cmd.setAfter(name.getText());
                cmd.execute();
                commandHistory.add(cmd);
            }
        });
    }

    public void setValues(WorldEntity entity) {
        this.entity = entity;
        if(entity == null) {
            active.setDisabled(true);
            name.setDisabled(true);
            return;
        }else{
            active.setDisabled(false);
            name.setDisabled(false);
        }
        active.setChecked(entity.active);
        name.setText(entity.name);
    }

}
