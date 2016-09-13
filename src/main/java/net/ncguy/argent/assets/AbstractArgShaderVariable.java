package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Guy on 13/09/2016.
 */
public abstract class AbstractArgShaderVariable {

    public String name;
    public Class<?> type;

    public AbstractArgShaderVariable(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public abstract void bind(ShaderProgram shader);

}
