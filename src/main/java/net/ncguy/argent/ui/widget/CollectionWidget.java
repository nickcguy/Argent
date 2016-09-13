package net.ncguy.argent.ui.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import net.ncguy.argent.ui.Icons;

import java.util.function.Consumer;

/**
 * Created by Guy on 12/09/2016.
 */
public class CollectionWidget extends Table {

    ScrollPane scroller;
    Table parent;

    Button displayBtn;
    Image img;
    Drawable up, over;
    Table headerTable;
    Table contentTable;
    CollapsibleWidget contentWidget;
    boolean fillRight = true;
    private Consumer<Table> headerBuilder;

    public CollectionWidget() {
        this(true, null);
    }

    public CollectionWidget(boolean fillRight) {
        this(fillRight, null);
    }

    public CollectionWidget(Consumer<Table> headerBuilder) {
        this(true, headerBuilder);
    }

    public CollectionWidget(boolean fillRight, Consumer<Table> headerBuilder) {
        super(VisUI.getSkin());
        this.fillRight = fillRight;
        this.headerBuilder = headerBuilder;
        init();
    }

    private void init() {
        initContainer();
        initHeader();
        initContent();
        assemble();
        update();
    }

    private void initContainer() {
        parent = new Table(VisUI.getSkin());
        scroller = new ScrollPane(parent);
    }

    @Override
    protected void sizeChanged() {
//        update();
    }

    @Override
    protected void childrenChanged() {
        update();
    }

    private void update() {
        setFillParent(true);
    }

    private void initHeader() {
        headerTable = new Table(VisUI.getSkin());
        headerTable.setBackground(new NinePatchDrawable(Icons.Node.TABLEHEADER.patch()));
        up = Icons.Node.ARROW.drawable();
        over = Icons.Node.ARROW_HOVER.drawable();
        img = new Image();
        displayBtn = new TextButton("", VisUI.getSkin());
        displayBtn.addActor(img);
        displayBtn.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                changeIcon(true);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                changeIcon(false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(event.isStopped()) return super.touchDown(event, x, y, pointer, button);
                toggle();
                return true;
            }
        });
        float imgSize = 16;
        img.setSize(imgSize, imgSize);
        displayBtn.setSize(imgSize, imgSize);
        changeIcon(false);
        headerTable.add(displayBtn).size(imgSize).left().padRight(4);
        if(headerBuilder != null) headerBuilder.accept(headerTable);
        if(fillRight) headerTable.add("").growX().row();
    }

    private void initContent() {
        contentTable = new Table(VisUI.getSkin());
        contentTable.defaults().growX().uniform().top();
        contentWidget = new CollapsibleWidget(contentTable);
    }

    private void assemble() {
        parent.add(headerTable).growX().top().row();
        parent.add(contentWidget).growX().top().row();
        parent.add("").grow().row();
        add(scroller).grow().row();
    }

    private void changeIcon(boolean hover) {
        img.setDrawable(hover ? over : up);
    }

    private void toggle() {
        contentWidget.setCollapsed(!contentWidget.isCollapsed());
        update();
        rotateIcon();
    }

    private void rotateIcon() {
        float angle = contentWidget.isCollapsed() ? 90 : 0;
        img.setOrigin(img.getWidth()/2, img.getHeight()/2);
        img.addAction(Actions.rotateTo(angle, .1f));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public Table c() {
        return contentTable;
    }
}
