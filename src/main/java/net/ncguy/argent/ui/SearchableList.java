package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.TextureCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Guy on 21/07/2016.
 */
public class SearchableList<T> extends Group {

    private boolean ready = false;
    private ArrayList<Item<T>> items;
    private Consumer<Item<T>> changeListener;
    private Drawable bg = new TextureRegionDrawable(new TextureRegion(TextureCache.pixel()));
    private ClickListener stageClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stage_onClick(event, x, y);
        }
    };
    private Item<T> selectedItem;

    public SearchableList() {
        this.items = new ArrayList<>();
        initUI();
        ready = true;
        resizeElements();
    }

    // UI
    protected TextField searchField;
    protected ScrollPane scroller;
    protected Tree itemTree;

    protected void initUI() {
        this.searchField = new TextArea("", VisUI.getSkin());
        this.itemTree = new Tree(VisUI.getSkin());
        this.scroller = new ScrollPane(this.itemTree);

        this.searchField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final String q = SearchableList.this.searchField.getText();
                SearchableList.this.items.forEach(i -> i.discernVisibility(q));
            }
        });

        this.addActor(this.searchField);
        this.addActor(this.scroller);

        this.itemTree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SearchableList.this.select();
            }
        });
    }

    private void stage_onClick(InputEvent event, float x, float y) {
        Vector2 stageCoords = new Vector2(x, y);
        Rectangle stageBounds = new Rectangle();
        stageBounds.setPosition(localToStageCoordinates(new Vector2(0, 0)));
        stageBounds.setSize(getWidth(), getHeight());
        System.out.println(stageCoords.toString());
        System.out.println(stageBounds.toString());
        boolean contains = stageBounds.contains(stageCoords);
        System.out.println(contains);
        if(!contains)
            hide();
    }

    protected void resizeElements() {
        this.searchField.pack();
        this.searchField.setWidth(getWidth()-6);
        this.searchField.setPosition(3, getHeight()-(this.searchField.getHeight()+3));

        this.scroller.setSize(getWidth()-6, getHeight()-(this.searchField.getHeight()+9));
        this.scroller.setPosition(3, 3);

        this.itemTree.setPosition(0, 0);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (ready) resizeElements();
    }

    public void setPosition(Vector2 pos) {
        setPosition(pos.x, pos.y);
    }

    public float getDesiredHeight() {
        return getDesiredHeight(400);
    }

    public float getDesiredHeight(int max) {
        float tHeight = (items.size()*40)+this.searchField.getHeight()+12;
        return Math.min(max, tHeight);
    }

    public void show(Stage stage) {
        show(stage, AppUtils.Input.getPackedCursorPos());
    }
    public void show(Stage stage, Vector2 pos) {
        show(stage, pos.x, pos.y);
    }
    public void show(Stage stage, float x, float y) {
        this.clearActions();
        stage.addActor(this);
        stage.addListener(stageClickListener);
        this.setPosition(x, y);
        this.setSize(300, getDesiredHeight(350));
        AppUtils.Stage2d.keepWithinStage(this);
        this.addAction(Actions.fadeIn(.3f));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        bg.draw(batch, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    public void select() {
        Tree.Node sel = this.itemTree.getSelection().first();
        if(sel != null) select(sel);
    }

    public void select(Tree.Node node) {
        if(this.changeListener == null) return;
        Actor a = node.getActor();
        if(a instanceof Item) {
            this.changeListener.accept((Item<T>) a);
            this.selectedItem = (Item<T>) a;
            hide();
        }else{
            this.selectedItem = null;
        }
    }

    public Item<T> selectedItem() { return selectedItem; }

    public void hide() {
        if(this.getStage() == null) return;
        this.clearActions();
        this.getStage().removeListener(this.stageClickListener);
        this.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.run(this::remove)));
    }

    public void addItem(Item<T> item) {
        this.items.add(item);
        this.itemTree.add(new Tree.Node(item));
        item.setWidth(this.itemTree.getWidth());
        item.onToggleVisibility = this::onToggleVisibility;
    }

    public void addItems(Item<T>... items) {
        for (Item<T> item : items) addItem(item);
    }

    private void onToggleVisibility(boolean visibility) {
        itemTree.invalidate();
    }

    public void setChangeListener(Consumer<Item<T>> changeListener) {
        this.changeListener = changeListener;
    }

    public static class Item<T> extends Group {

        private Drawable icon;
        private Image image;
        private Label label;
        private Set<String> keywords;
        private Consumer<Boolean> onToggleVisibility;
        private boolean ready = false;

        public final T value;

        public Item(Drawable icon, String text, T value, String... keywords) {
            this.icon = icon;
            this.label = new Label(text, VisUI.getSkin());
            this.image = new Image(icon);
            this.value = value;
            this.keywords = new LinkedHashSet<>();
            Collections.addAll(this.keywords, keywords);
            this.addActor(this.image);
            this.addActor(this.label);
            ready = true;
            setHeight(36);
        }

        protected void resizeElements() {
            this.image.setBounds(2, 2, 32, 32);
            this.label.setBounds(36, 16, 0, 0);
        }

        @Override
        protected void sizeChanged() {
            super.sizeChanged();
            if(ready) resizeElements();
        }

        public boolean match(String query) {
            String[] querySegs = query.split(" ");
            StringBuilder sb = new StringBuilder();
            this.keywords.forEach(k -> sb.append(k).append(" "));
            String keywordStr = sb.toString();
            for(String seg : querySegs) {
                if(!keywordStr.contains(seg))
                    return false;
            }
            return true;
        }

        public void discernVisibility(String query) {
            setVisible(match(query));
        }

        @Override
        public void setVisible(boolean visible) {
            setHeight(visible ? 36 : -4);
            super.setVisible(visible);
            if(onToggleVisibility != null) onToggleVisibility.accept(visible);
        }

    }
}

