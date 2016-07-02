package net.ncguy.argent.editor.swing.config.descriptors.builders;

import com.badlogic.gdx.math.MathUtils;
import net.ncguy.argent.core.BasicEntry;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Guy on 01/07/2016.
 */
public class SwingComponentBuilder extends AbstractComponentBuilder {

    private static SwingComponentBuilder instance;
    public static SwingComponentBuilder instance() {
        if (instance == null)
            instance = new SwingComponentBuilder();
        return instance;
    }

    private SwingComponentBuilder() {}


    @Override
    public JCheckBox buildCheckBox(Map<String, BasicEntry<Class<?>, Object>> params) {
        JCheckBox checkBox = new JCheckBox();

        Boolean selected = getValue(Boolean.class, params.get("selected"));
        String name = getValue(String.class, params.get("name"));

        checkBox.setText(name);
        checkBox.setSelected(selected);

        return checkBox;
    }

    @Override
    public JTextField buildTextField(Map<String, BasicEntry<Class<?>, Object>> params) {
        JTextField field = new JTextField();

        String text = getValue(String.class, params.get("text"));
        field.setText(text);

        return field;
    }

    @Override
    public JComboBox buildComboBox(Map<String, BasicEntry<Class<?>, Object>> params) {
        JComboBox box = new JComboBox();
        DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();

        List items = getValue(List.class, params.get("items"));
        Integer index = getValue(Integer.class, params.get("selectedindex"));

        model.removeAllElements();
        items.forEach(model::addElement);
        index = MathUtils.clamp(index, 0, items.size()-1);
        model.setSelectedItem(items.get(index));

        return box;
    }

    @Override
    public JSpinner buildNumberSelector(Map<String, BasicEntry<Class<?>, Object>> params) {

        Integer min = getValue(Integer.class, params.get("min"));
        Integer max = getValue(Integer.class, params.get("max"));
        Integer precision = getValue(Integer.class, params.get("precision"));
        Integer value = getValue(Integer.class, params.get("value"));

        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(min);
        model.setMaximum(max);
        model.setValue(value);

        System.out.printf("min: %s, max: %s\n", min, max);

        JSpinner spinner = new JSpinner(model);
//        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
//        editor.getFormat().setMinimumFractionDigits(precision);
        return spinner;
    }
}
