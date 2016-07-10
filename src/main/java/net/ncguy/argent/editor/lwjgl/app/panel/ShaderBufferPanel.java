package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.ui.BufferWidget;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Guy on 09/07/2016.
 */
public class ShaderBufferPanel<T> extends AbstractPanel<T> {

    public ShaderBufferPanel(GameWorld.Generic<T> gameWorld) {
        super(gameWorld);
    }

    private List<BufferWidget> widgetList;
    private Table table;
    private ScrollPane scroller;

    @Override
    protected AbstractPanel ui() {
        widgetList = new ArrayList<>();
        table = new Table(skin);
        scroller = new ScrollPane(table, skin);
        addActor(scroller);
        return this;
    }

    @Override
    protected AbstractPanel listeners() {
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Stack<WorldRenderer.BufferViewPack> packStack = gameWorld.renderer().bufferViews();
        table.clear();
        while(!packStack.isEmpty()) {
            WorldRenderer.BufferViewPack pack = packStack.pop();
            BufferWidget buffer = new BufferWidget(pack.name, skin);
            buffer.setSprite(pack.sprite);
            Vector2 screenSize = AppUtils.Graphics.getPackedSize();
            table.add(buffer).width(screenSize.x/4).height(screenSize.y/4).row();
        }
    }

    private void invalidateTable() {
    }

    @Override
    protected AbstractPanel select(Object obj) {
        widgetList.forEach(Actor::remove);
        widgetList.clear();
        gameWorld.renderer().bufferViews().forEach(s -> widgetList.add(new BufferWidget(s.name, skin)));
        return this;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        resizeElements();
    }

    protected void resizeElements() {
        if(scroller != null) {
            scroller.setBounds(0, 0, getWidth(), getHeight());
        }

        if(table != null) {
            table.pack();
            table.setX(5);
            table.setWidth(scroller.getWidth()-10);
            table.setY(table.getHeight());
            reselect();
        }
    }

}
