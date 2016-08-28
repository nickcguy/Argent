package net.ncguy.argent.render.custom;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.render.AbstractWorldRenderer;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Guy on 15/07/2016.
 */
public class CustomWorldRenderer<T extends WorldEntity> extends AbstractWorldRenderer<T> {

    List<BufferRenderer<T>> bufferRenderers;
    transient BufferRenderer<T> finalRenderer;
    int firstId = 4;

    public CustomWorldRenderer(GameWorld<T> world) {
        super(world);
        bufferRenderers = new ArrayList<>();
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        Stack<BufferRenderer<T>> stack = toStack();
        final int[] mutableId = new int[]{firstId};
        while(!stack.isEmpty())
            stack.pop().prepare().render().attachBufferToShader(finalRenderer.shaderProgram, mutableId);
        finalRenderer.render(false);
        batch.begin(camera());
        additionalRenderers.forEach(r -> r.render(batch, delta));
        batch.end();
    }

    public Stack<BufferRenderer<T>> toStack() {
        Stack<BufferRenderer<T>> stack = new Stack<>();
        for(int i = bufferRenderers.size()-2; i >= 0; i--)
            stack.push(bufferRenderers.get(i));
        finalRenderer = bufferRenderers.get(bufferRenderers.size()-1);
        return stack;
    }

    @Override
    public void dispose() {
    }
}
