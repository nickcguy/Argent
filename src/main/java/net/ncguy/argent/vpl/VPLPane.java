package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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

    public VPLPane(VPLContainer parent) {
        this.parent = parent;
        bg = DrawableFactory.grid();
        shader = AppUtils.Shader.loadShader("2d/graph");

        Argent.addOnKeyDown(keycode -> {
            if(keycode == Input.Keys.P) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    shader = AppUtils.Shader.loadShader("2d/graph");
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram current = batch.getShader();
        batch.setShader(shader);
        bg.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.draw(TextureCache.white(), -2, -2, 6, 6);
        super.draw(batch, parentAlpha);
        batch.setShader(current);
    }

    public Vector2 getStagePosition() {
        return localToStageCoordinates(new Vector2(0, 0));
    }

    public int getSafeX() {
        Vector3 unproject = parent.unproject(new Vector3(getX(), getY(), 0));
        return (int) unproject.x;
    }
    public int getSafeY() {
        return (int)getY();
    }
    public int getSafeWidth() {
        return (int)getWidth();
    }
    public int getSafeHeight() {
        return (int)getHeight();
    }

}
