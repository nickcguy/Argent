package net.ncguy.argent.data.config;

/**
 * Created by Guy on 15/07/2016.
 */
public enum ConfigControl {
    TEXTFIELD,
    CHECKBOX,
    COMBOBOX,
    NUMBERSELECTOR,
    COLOURPICKER,
    ;
    ConfigControl() {
        this(null);
    }
    ConfigControl(ConfigControlDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    private ConfigControlDescriptor descriptor;
    public ConfigControlDescriptor descriptor() { return descriptor; }
}
