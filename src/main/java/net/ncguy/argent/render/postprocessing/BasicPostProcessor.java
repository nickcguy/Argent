package net.ncguy.argent.render.postprocessing;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 31/07/2016.
 */
public class BasicPostProcessor extends PostProcessor {

    protected String shaderPrefix;
    protected ShaderProgram shaderProgram;

    protected SpriteBatch spriteBatch;

    public BasicPostProcessor(String shaderPrefix, int attachments) {
        super(attachments);
        this.shaderPrefix = shaderPrefix;
    }

    @Override
    protected SpriteBatch spriteBatch() {
        if(spriteBatch == null)
            spriteBatch = new SpriteBatch(1000, shaderProgram = AppUtils.Shader.loadShader(shaderPrefix));
        return spriteBatch;
    }
}
