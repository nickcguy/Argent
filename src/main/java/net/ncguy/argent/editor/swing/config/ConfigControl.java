package net.ncguy.argent.editor.swing.config;

import net.ncguy.argent.editor.swing.config.descriptors.*;

/**
 * Created by Guy on 01/07/2016.
 */
public enum ConfigControl {
    TEXTFIELD(new TextFieldDescriptor()),
    CHECKBOX(new CheckBoxDescriptor()),
    COMBOBOX(new ComboBoxDescriptor()),
    NUMBERSELECTOR(new NumberSelectorDescriptor()),
    ;

    ConfigControl(ControlConfigDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    public final ControlConfigDescriptor descriptor;

}
