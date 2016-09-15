package net.ncguy.argent.ui;

import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;

/**
 * Created by Guy on 27/07/2016.
 */
public class FloatFieldWithLabel extends TextFieldWithLabel {
    public FloatFieldWithLabel(String text, int width, boolean allowNegative) {
        super(text, width);
        textField.setTextFieldFilter(new FloatDigitsOnlyFilter(allowNegative));
    }

    public FloatFieldWithLabel(String text, int width) {
        this(text, width, true);
    }

    public float getFloat() {
        try {
            if (getText().isEmpty() ||
                    (getText().length() == 1 && (getText().startsWith("-") || getText().startsWith("."))))
                return 0;
            if (getText().startsWith("-.")) {
                int sel = textField.getCursorPosition();
                setText("-0." + getText().substring(2));
                textField.setCursorPosition(sel);
            }
            return Float.parseFloat(getText());
        }catch (Exception e) {
            return 1.0f;
        }
    }

}
