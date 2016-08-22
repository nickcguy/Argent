package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import net.ncguy.argent.Argent;
import net.ncguy.argent.ui.DrawableFactory;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.TextureCache;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLPane extends Group {

    Drawable bg;
    ShaderProgram shader;
    private VPLContainer parent;
    protected String[] tag;
    VPLGraph graph;
    Rectangle gridBounds;

    public VPLPane(VPLContainer parent, String... tags) {
        this.parent = parent;
        this.tag = tags;
        bg = DrawableFactory.grid();
        shader = AppUtils.Shader.loadShader("2d/graph");
        graph = new VPLGraph(this, tag);

        Argent.addOnKeyDown(keycode -> {
            if(keycode == Input.Keys.P) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    shader = AppUtils.Shader.loadShader("2d/graph");
                }
            }
        });
        addActor(graph);
        gridBounds = new Rectangle(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
        graph.bounds = gridBounds;
    }

    @Override
    protected void sizeChanged() {
        graph.setWidth(getWidth());
        graph.setHeight(getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        gridBounds.set(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
        batch.flush();
        ScissorStack.pushScissors(gridBounds);
        ShaderProgram current = batch.getShader();
        batch.setShader(shader);
        bg.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.draw(TextureCache.white(), (getX()+(getWidth()/2))-2, (getY()+(getHeight()/2))-2, 6, 6);
        super.draw(batch, parentAlpha);
        batch.flush();
        ScissorStack.popScissors();
        batch.setShader(current);
    }


}
