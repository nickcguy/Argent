package net.ncguy.argent.ui;

import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter;

/**
 * Created by Guy on 04/08/2016.
 */
public class IntFieldWithLabel extends TextFieldWithLabel {

    public IntFieldWithLabel(String text, int width, boolean allowNegative) {
        super(text, width);
        textField.setTextFieldFilter(new IntDigitsOnlyFilter(allowNegative));
    }

    public IntFieldWithLabel(String text, int width) {
        this(text, width, true);
    }

    public int getInt() {
        if(getText().isEmpty() ||
                (getText().length() == 1 && (getText().startsWith("-") || getText().startsWith("."))))
            return 0;
        try {
            return Integer.parseInt(getText());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
