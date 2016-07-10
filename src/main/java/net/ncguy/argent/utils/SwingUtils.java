package net.ncguy.argent.utils;


import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 28/06/2016.
 */
public class SwingUtils {

    public static List<Component> getAllComponents(final Container c) {
        List<Component> list = new ArrayList<>();
        Component[] comps = c.getComponents();
        for (Component comp : comps) {
            list.add(comp);
            if(comp instanceof Container)
                list.addAll(getAllComponents((Container) comp));
        }
        return list;
    }

    public static List<Component> getAllComponentsOfClass(final Container c, final Class cls) {
        List<Component> list = new ArrayList<>();
        Component[] comps = c.getComponents();
        for (Component comp : comps) {
            if(comp.getClass().isAssignableFrom(cls)) list.add(comp);
            if(comp instanceof Container)
                list.addAll(getAllComponentsOfClass((Container) comp, cls));
        }
        return list;
    }

    public static DefaultFormatter getSpinnerFormatter(JSpinner spinner) {
        JComponent comp = spinner.getEditor();
        JFormattedTextField field = (JFormattedTextField)comp.getComponent(0);
        return (DefaultFormatter)field.getFormatter();
    }

}
