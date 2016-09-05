package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import net.ncguy.argent.entity.components.model.ModelComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 05/09/2016.
 */
public class ArgentShaderProvider extends DefaultShaderProvider {

    public static Map<ModelComponent, Shader> componentShaders = new HashMap<>();

    @Override
    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;

        Object obj = renderable.userData;
        if(obj instanceof ModelComponent) {
            ModelComponent c = (ModelComponent)obj;
            if(componentShaders.containsKey(c))
                return componentShaders.get(c);
            final Shader shader = createShader(renderable);
            shader.init();
            componentShaders.put(c, shader);
            return shader;
        }
        final Shader shader = createShader(renderable);
        shader.init();
        return shader;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        if(renderable.userData instanceof ModelComponent) {
            ModelComponent model = (ModelComponent) renderable.userData;
            try {
                if (model.shader.shaderProgram().isCompiled())
                    return new ArgentShader(model.shader, renderable);
            }catch (NullPointerException npe) {}
        }
        return super.createShader(renderable);
    }

    public static void clearCache() {
        componentShaders.forEach((c, s) -> s.dispose());
        componentShaders.clear();
    }
}
