package net.ncguy.argent.editor.views.shader;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 31/08/2016.
 */
public class NewShaderDialog extends VisDialog {

    private VisValidatableTextField name;
    private TextButton createBtn;

    @Inject
    ProjectManager manager;

    public NewShaderDialog() {
        super("New Shader");
        ArgentInjector.inject(this);
        addCloseButton();

        Table root = new Table(VisUI.getSkin());
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        name = new VisValidatableTextField(s -> !s.isEmpty());

        root.add("Name").padRight(4);
        root.add(name).growX().row();

        createBtn = new TextButton("Create Shader", VisUI.getSkin());
        root.add(createBtn).width(93).height(25).colspan(2);
        attachListeners();

        pack();
    }

    private void attachListeners() {
        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(name.isInputValid()) {
                    ArgShader argShader = new ArgShader("", "");
                    argShader.name = name.getText();
                    manager.current().addShader(argShader);
                    argShader.save();
                    hide();
                }
            }
        });
    }


}
