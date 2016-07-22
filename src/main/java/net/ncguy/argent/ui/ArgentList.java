package net.ncguy.argent.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Guy on 21/07/2016.
 */
public class ArgentList<T> extends List<ArgentList.ArgentListElement<T>> {

    Runnable onRightClick;

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

    protected void init() {
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArgentListElement<T> selected = getSelected();
                if(selected != null) selected.select();
            }
        });
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArgentListElement<T> e = getElementAtHeight(y);
                if(e != null) e.rightClick();
                else rightClick();
            }
        });
    }

    public void rightClick() { if(this.onRightClick != null) this.onRightClick.run(); }
    public Runnable onRightClick() { return onRightClick; }
    public void onRightClick(Runnable onRightClick) { this.onRightClick = onRightClick; }

    public ArgentListElement<T> getElementAtHeight(float y) {
        Array<ArgentListElement<T>> items = getItems();
        y = getHeight()-y;
        int index = (int) Math.floor(y / getItemHeight());
        if(index >= 0 && index < items.size)
            return items.get(index);
        return null;
    }

    public void select(T obj) {
        if(!containsItem(obj)) return;
        setSelected(new ArgentListElement<>(obj));
    }

    public int getItemIndex(T obj) {
        if(!containsItem(obj)) return -1;
        return getItems().indexOf(new ArgentListElement<>(obj), false);
    }

    public void addItem(T obj) {
        addItem(obj, null);
    }

    public void addItem(T obj, Consumer<T> onSelect) {
        addItem(new ArgentListElement<>(obj, onSelect));
    }

    public void addItem(T obj, Consumer<T> onSelect, Consumer<ArgentListElement<T>> onRightClick) {
        ArgentListElement<T> e = new ArgentListElement<>(obj, onSelect);
        e.onRightClick(onRightClick);
        addItem(e);
    }

    public void addItem(ArgentListElement<T> item) {
        getItems().add(item);
    }

    public boolean containsItem(T item) {
        return getItems().contains(new ArgentListElement<>(item), false);
    }

    public void removeItem(T obj) {
        getItems().removeValue(new ArgentListElement<>(obj), false);
    }

    public ArrayList<T> getRawItems() {
        final ArrayList<T> items = new ArrayList<>();
        getItems().forEach(i -> items.add(i.object));
        return items;
    }

    public static class ArgentListElement<T> {
        public final T object;
        private Consumer<T> onSelect;
        private Function<T, String> getString;
        private Consumer<ArgentListElement<T>> onRightClick;

        public ArgentListElement(T object) {
            this.object = object;
        }

        public ArgentListElement(T object, Consumer<T> onSelect) {
            this.object = object;
            this.onSelect = onSelect;
        }

        public void select() { if(onSelect != null) onSelect.accept(object); }
        public void rightClick() { if(onRightClick != null) onRightClick.accept(this); }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == this.object) return true;
            if(obj instanceof ArgentListElement)
                if(((ArgentListElement)obj).object == this.object) return true;
            return super.equals(obj);
        }

        @Override
        public String toString() {
            if(getString != null) return getString.apply(object);
            return object.toString();
        }

        public Consumer<T> onSelect() { return onSelect; }
        public void onSelect(Consumer<T> onSelect) { this.onSelect = onSelect; }

        public Function<T, String> getString() { return getString; }
        public void getString(Function<T, String> getString) { this.getString = getString; }

        public Consumer<ArgentListElement<T>> onRightClick() { return onRightClick; }
        public void onRightClick(Consumer<ArgentListElement<T>> onRightClick) { this.onRightClick = onRightClick; }

    }
}
