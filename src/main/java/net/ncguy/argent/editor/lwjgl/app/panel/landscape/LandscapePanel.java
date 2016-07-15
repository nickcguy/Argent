package net.ncguy.argent.editor.lwjgl.app.panel.landscape;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.editor.lwjgl.app.panel.AbstractPanel;
import net.ncguy.argent.editor.shared.config.builders.AbstractComponentBuilder;
import net.ncguy.argent.editor.shared.config.builders.GDXComponentBuilder;
import net.ncguy.argent.world.GameWorld;
import net.ncguy.argent.world.landscape.Terrain;

/**
 * Created by Guy on 14/07/2016.
 */
public class LandscapePanel<T> extends AbstractPanel<T> {

    protected Terrain terrain;
    protected Tree configTree;
    protected ScrollPane configScroller;
    protected TextButton addLandscapeBtn;
    protected AbstractComponentBuilder builder = GDXComponentBuilder.instance();


    public LandscapePanel(Stage stage, GameWorld.Generic<T> gameWorld) {
        super(stage, gameWorld);
        sizeChanged();
    }

    @Override
    protected AbstractPanel ui() {
        if(gameWorld.hasLandscape()) {
            this.terrain = gameWorld.terrain;
            configTree = new Tree(skin);
            configScroller = new ScrollPane(configTree);
            addActor(configScroller);
            return this;
        }
        this.terrain = null;
        addActor(addLandscapeBtn = new TextButton("Add landscape", skin));
        return this;
    }

    @Override
    protected AbstractPanel listeners() {
        if(gameWorld.hasLandscape()) {
            buildConfig();
            return this;
        }
        addLandscapeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new Dialog("Landscape Size", skin);
                Spinner cellWidthField = new Spinner("Cell Width", new IntSpinnerModel(100, 1, 1024, 1));
                Spinner cellHeightField = new Spinner("Cell Height", new IntSpinnerModel(100, 1, 1024, 1));
                Spinner gridWidthField = new Spinner("Cells Wide", new IntSpinnerModel(64, 1, Short.MAX_VALUE, 1));
                Spinner gridHeightField = new Spinner("Cells Long", new IntSpinnerModel(64, 1, Short.MAX_VALUE, 1));
                dialog.add(cellWidthField).width(192);
                dialog.add(cellHeightField).width(192).row();
                dialog.add(gridWidthField).width(192);
                dialog.add(gridHeightField).width(192).row();
                dialog.setWidth(400);
                TextButton createBtn = new TextButton("Generate Landscape", skin);
                createBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        int cellWidth = ((IntSpinnerModel)cellWidthField.getModel()).getValue();
                        int cellHeight = ((IntSpinnerModel)cellHeightField.getModel()).getValue();
                        int gridWidth = ((IntSpinnerModel)gridWidthField.getModel()).getValue();
                        int gridHeight = ((IntSpinnerModel)gridHeightField.getModel()).getValue();
                        gameWorld.addLandscape(Vector3.Zero, gridWidth, gridHeight, cellWidth, cellHeight);
                        dialog.hide();
                        reconstructUI();
                    }
                });
                dialog.button(createBtn);
                dialog.show(stage);
            }
        });
        return this;
    }

    public void reconstructUI() {
        clear();
        ui();
        listeners();
        sizeChanged();
    }

    private void buildConfig() {
        builder.compileSet(configTree, terrain.getConfigAttrs());
        configTree.getRootNodes().forEach(node -> node.setExpanded(true));
    }

    @Override
    protected AbstractPanel select(T obj) {
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        stage.setDebugAll(true);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if(addLandscapeBtn != null) {
            addLandscapeBtn.pack();
            addLandscapeBtn.setPosition((getWidth()/2)-(addLandscapeBtn.getWidth()/2), (getHeight()/2)-(addLandscapeBtn.getHeight()/2));
        }
        if(configScroller != null) {
            configScroller.setBounds(0, 0, 384, getHeight());
            configTree.setPosition(0, 0);
            configTree.setWidth(configScroller.getWidth());
            buildConfig();
        }
    }
}
