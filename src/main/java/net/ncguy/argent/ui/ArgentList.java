package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import net.ncguy.argent.core.VarRunnables;

import java.util.ArrayList;

/**
 * Created by Guy on 04/07/2016.
 */
public class ArgentList<T> extends List<ArgentList.ArgentListElement<T>> {

    public ArgentList(Skin skin) {
        super(skin);
        init();
    }

    public ArgentList(Skin skin, String styleName) {
        super(skin, styleName);
        init();
    }

    public ArgentList(ListStyle style) {
        super(style);
        init();
    }

    private void init() {
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArgentListElement<T> selected = getSelected();
                if(selected != null) selected.onSelect();
            }
        });
    }

    public void addItem(T obj) {
        addItem(obj, null);
    }

    public void addItem(T obj, VarRunnables.VarRunnable<T> onSelect) {
        getItems().add(new ArgentListElement<>(obj, onSelect));
    }

    public boolean containsItem(T obj) {
        return getItems().contains(new ArgentListElement<>(obj), false);
    }

    public void removeItem(T obj) {
        getItems().removeValue(new ArgentListElement<>(obj), false);
    }

    public java.util.List<T> getRawItems() {
        java.util.List<T> items = new ArrayList<>();
        getItems().forEach(i -> items.add(i.obj));
        return items;
    }

    public static class ArgentListElement<T> {
        public T obj;
        VarRunnables.VarRunnable<T> onSelect;

        public ArgentListElement(T obj) {
            this.obj = obj;
        }

        public ArgentListElement(T obj, VarRunnables.VarRunnable<T> onSelect) {
            this.obj = obj;
            this.onSelect = onSelect;
        }

        public void onSelect() {
            if(onSelect != null) onSelect.run(obj);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == this.obj) return true;

            if(obj instanceof ArgentListElement)
                if(((ArgentListElement)obj).obj == this.obj) return true;

            return super.equals(obj);
        }

        @Override
        public String toString() {
            return obj.toString();
        }
    }

}
