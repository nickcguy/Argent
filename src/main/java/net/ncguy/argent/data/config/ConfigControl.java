package net.ncguy.argent.data.config;

import net.ncguy.argent.data.config.descriptors.*;

/**
 * Created by Guy on 15/07/2016.
 */
public enum ConfigControl {
    CHECKBOX(new CheckBoxDescriptor()),
    COLOURPICKER(new ColourPickerDescriptor()),
    COMBOBOX(new ComboBoxDescriptor()),
    NUMBERSELECTOR(new NumberSelectorDescriptor()),
    SELECTIONLIST(new SelectionListDescriptor()),
    TEXTFIELD(new TextFieldDescriptor()),
    ;
    ConfigControl() {
        this(null);
    }
    ConfigControl(ConfigControlDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    public final ConfigControlDescriptor descriptor;
}
