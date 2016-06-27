package net.ncguy.argent.editor.swing;

import javax.swing.*;

/**
 * Created by Guy on 27/06/2016.
 */
public interface IEditorPane<T> {

    IEditorPane<T> init(JMenuBar menuBar);
    JComponent getRootComponent();

}
