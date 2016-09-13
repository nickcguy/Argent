package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.function.Consumer;

/**
 * Created by Guy on 13/09/2016.
 */
public class ArgShaderVariable extends AbstractArgShaderVariable {

    Consumer<ShaderProgram> binding;

    public ArgShaderVariable(String name, Class<?> type, Consumer<ShaderProgram> binding) {
        super(name, type);
        this.binding = binding;
    }

    @Override
    public void bind(ShaderProgram shader) {
        if(binding != null) binding.accept(shader);
    }
}
