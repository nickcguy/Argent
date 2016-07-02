package net.ncguy.argent.editor.swing.object;

import net.ncguy.argent.editor.swing.IEditorPane;
import net.ncguy.argent.world.GameWorld;

import javax.swing.*;

/**
 * Created by Guy on 28/06/2016.
 */
public class ObjectEditor<T> implements IEditorPane<T> {

    protected ObjectForm objectForm;
    protected GameWorld.Generic<T> gameWorld;

    public ObjectEditor(GameWorld.Generic<T> gameWorld) {
        this.gameWorld = gameWorld;
        SwingUtilities.invokeLater(() -> {
            objectForm = new ObjectForm<>(this);
        });
    }

    @Override
    public ObjectEditor<T> init(JMenuBar menuBar) {
        return this;
    }

    @Override
    public JComponent getRootComponent() {
        return objectForm.$$$getRootComponent$$$();
    }
}
