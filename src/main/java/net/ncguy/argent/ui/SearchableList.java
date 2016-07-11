package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.Color;
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
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.GdxUtils;
import net.ncguy.argent.utils.SpriteCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 07/07/2016.
 */
public class SearchableList<T> extends Group {

    private Skin skin;
    private boolean ready = false;
    private ArrayList<Item<T>> items;
    private VarRunnables.VarRunnable<Item<T>> changeListener;
    private ClickListener stageClickListener = new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stage_onClick(event, x, y);
        }
    };

    private Drawable bg = new TextureRegionDrawable(new TextureRegion(SpriteCache.pixel()));

    public SearchableList(Skin skin) {
        this.skin = skin;
        this.items = new ArrayList<>();
        initUI();
        ready = true;
        resizeElements();
    }

    // UI
    private TextField searchField;
    private ScrollPane scroller;
    private Tree itemTree;

    private void initUI() {
        searchField = new TextArea("", skin);
        itemTree = new Tree(skin);
        scroller = new ScrollPane(itemTree, skin);

        searchField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final String q = searchField.getText();
                items.forEach(i -> i.setVisible(i.match(q)));
            }
        });

        addActor(searchField);
        addActor(scroller);

        itemTree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                select();
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

    private void resizeElements() {
        searchField.pack();
        searchField.setSize(getWidth()-6, searchField.getHeight());
        searchField.setPosition(3, getHeight()-(searchField.getHeight()+3));
        scroller.setSize(getWidth()-6, getHeight()-(searchField.getHeight()+9));
        scroller.setPosition(3, 3);
        itemTree.setPosition(0, 0);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if(ready) resizeElements();
    }

    public void setPosition(Vector2 pos) {
        setPosition(pos.x, pos.y);
    }

    public float getDesiredHeight() {
        return getDesiredHeight(400);
    }
    public float getDesiredHeight(int max) {
        float tHeight = (items.size()*40)+searchField.getHeight()+12;
        return Math.min(max, tHeight);
    }

    public void show(Stage stage) {
        show(stage, AppUtils.Graphics.getPackedCursorPos());
    }

    public void show(Stage stage, Vector2 pos) {
        show(stage, pos.x, pos.y);
    }

    public void show(Stage stage, float x, float y) {
        clearActions();
        stage.addActor(this);
        stage.addListener(stageClickListener);
        setPosition(x, y);
        setSize(300, getDesiredHeight(350));
        GdxUtils.keepWithinStage(this);
        addAction(Actions.fadeIn(.3f));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.CYAN);
        bg.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    public void select() {
        Tree.Node sel = itemTree.getSelection().first();
        System.out.println(sel);
        if(sel != null) select(sel);

    }
    public void select(Tree.Node node) {
        Actor a = node.getActor();
        if(a instanceof Item) {
            changeListener.run((Item) a);
            hide();
        }
    }

    public void hide() {
        if(getStage() == null) return;
        clearActions();
        getStage().removeListener(stageClickListener);
        addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.run(this::remove)));
    }

    public void addItem(Item<T> item) {
        items.add(item);
        itemTree.add(new Tree.Node(item));
        item.setWidth(itemTree.getWidth());
        item.onToggleVisibility = this::onToggleVisibility;
    }

    public void addItems(Item... items) {
        for (Item item : items) addItem(item);
    }

    private void onToggleVisibility(boolean visibility) {
        itemTree.invalidate();
    }

    public void addChangeListener(VarRunnables.VarRunnable<Item<T>> changeListener) {
        this.changeListener = changeListener;
    }

    public static class Item<T> extends Group {

        private Skin skin;
        private Drawable icon;
        private Image image;
        private Label label;
        private Set<String> keywords;
        private VarRunnables.VarRunnable<Boolean> onToggleVisibility;
        private boolean ready = false;
        private T value;

        public Item(Skin skin, Drawable icon, String text, T value, String... keywords) {
            this.skin = skin;
            this.icon = icon;
            this.label = new Label(text, skin);
            this.image = new Image(icon);
            this.value = value;
            this.keywords = new LinkedHashSet<>();
            Collections.addAll(this.keywords, keywords);
            addActor(this.image);
            addActor(this.label);
            ready = true;
            setHeight(36);
        }

        public T value() { return value; }

        private void resizeElements() {
            this.image.setBounds(2, 2, 32, 32);
            this.label.setBounds(36, 16, 0, 0);
        }

        @Override
        protected void sizeChanged() {
            super.sizeChanged();
            if(ready) resizeElements();
        }

        public boolean match(String query) {
            String[] qs = query.split(" ");
            ArrayList<String> keywords = new ArrayList<>();
            keywords.addAll(this.keywords);
            Collections.addAll(keywords, this.label.getText().toString().split(" "));
            for (String keyword : keywords) {
                for(String q : qs) {
                    q = q.toLowerCase();
                    keyword = keyword.toLowerCase();
                    if(keyword.contains(q)) return true;
                }
            }
            return false;
        }

        @Override
        public void setVisible(boolean visible) {
            setHeight(visible ? 36 : -4);
            super.setVisible(visible);
            if(onToggleVisibility != null) onToggleVisibility.run(visible);
        }

        public static class Data<T> {
            private Drawable icon;
            private String text;
            private T value;
            private String[] keywords;

            public Data(Drawable icon, String text, T value, String... keywords) {
                this.icon = icon;
                this.text = text;
                this.value = value;
                this.keywords = keywords;
            }

            public Item compile(Skin skin) {
                return new Item<>(skin, icon, text, value, keywords);
            }

        }

    }

}
