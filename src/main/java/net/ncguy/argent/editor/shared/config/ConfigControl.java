package net.ncguy.argent.editor.shared.config;

import net.ncguy.argent.editor.shared.config.descriptors.*;

/**
 * Created by Guy on 01/07/2016.
 */
public enum ConfigControl {
    TEXTFIELD(new TextFieldDescriptor()),
    CHECKBOX(new CheckBoxDescriptor()),
    COMBOBOX(new ComboBoxDescriptor()),
    NUMBERSELECTOR(new NumberSelectorDescriptor()),
    SELECTIONLIST(new SelectionListDescriptor()),
    COLOURPICKER(new ColourPickerDescriptor()),
    ;

    ConfigControl(ControlConfigDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    public final ControlConfigDescriptor descriptor;

}
