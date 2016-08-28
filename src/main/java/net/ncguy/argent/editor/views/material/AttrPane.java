package net.ncguy.argent.editor.views.material;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.builders.AttributeBuilder;
import net.ncguy.argent.editor.views.MaterialViewer;
import net.ncguy.argent.editor.views.material.attributes.ColourAttrWidget;
import net.ncguy.argent.editor.views.material.attributes.TextureAttrWidget;
import net.ncguy.argent.event.MaterialComponentChangeEvent;
import net.ncguy.argent.ui.ArgentTabbedPane;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 04/08/2016.
 */
public class AttrPane extends Table implements MaterialComponentChangeEvent.MaterialComponentChangeListener {

    private MaterialViewer viewer;
    private EditorUI editorUI;
    private ArgentTabbedPane attrTabs;
    private Table attrGrid;
    private ScrollPane attrScroller, tabScroller;
    private TextButton addAttrBtn;
    private SearchableList<Attribute> mtlAttributeList;

    private static List<Long> attrTypes;
    private ArgMaterial argMtl;

    public AttrPane(MaterialViewer viewer, EditorUI editorUI) {
        super(VisUI.getSkin());
        Argent.event.register(this);
        this.viewer = viewer;
        this.editorUI = editorUI;
        this.attrGrid = new Table(VisUI.getSkin());
        this.attrTabs = new ArgentTabbedPane();
        this.addAttrBtn = new TextButton("Add Attribute", VisUI.getSkin());
        this.attrScroller = new ScrollPane(this.attrGrid);
        this.tabScroller = new ScrollPane(this.attrTabs);

        this.tabScroller.setScrollingDisabled(false, true);
//        this.tabScroller.setFlickScroll(false);
        this.tabScroller.setScrollBarPositions(false, true);

        this.attrScroller.setScrollingDisabled(true, false);

        add(this.tabScroller).expandX().fillX().row();
        add(this.attrScroller).expand().fill().row();
        add(this.addAttrBtn).expandX().fillX().bottom().pad(4).row();

        mtlAttributeList = new SearchableList<>();

        this.addAttrBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                mtlAttributeList.show(editorUI, pos.x, pos.y);
            }
        });

//            Field typeField = Attribute.class.getDeclaredField("types");
        if(attrTypes == null) {
            attrTypes = new ArrayList<>();
            Array<String> types = ReflectionUtils.getValue(null, Attribute.class, "types", Array.class);
            if (types != null)
                types.forEach(a -> attrTypes.add(Attribute.getAttributeType(a)));
        }

        mtlAttributeList.setChangeListener(attr -> {
            ArgMaterial mtl = viewer.getArgMaterial();
            if(mtl == null) return;
            Material m = mtl.getAsset();
            if(m == null) return;
            m.set(attr.value);
            generateConfigWidgets(mtl);
        });
    }

    public void onMaterialSelect(ArgMaterial argMtl) {
        this.argMtl = argMtl;
        generateConfigWidgets(argMtl);
        populateNewList(argMtl);
    }

    protected void populateNewList(ArgMaterial argMtl) {
        mtlAttributeList.clearItems();
        Material mtl = argMtl.getAsset();
        attrTypes.stream().filter(t -> !mtl.has(t)).forEach(t -> {
            String alias = Attribute.getAttributeAlias(t);
            mtlAttributeList.addItem(new SearchableList.Item<>(null, alias, AttributeBuilder.buildAttribute(t)));
        });
    }

    protected void generateConfigWidgets(ArgMaterial argMtl) {
        List<AttrWidget> list = new ArrayList<>();
        attrTabs.removeAll();
        argMtl.getAsset().iterator().forEachRemaining(attr ->
            generateWidget(list, argMtl.getAsset(), attr));
        attrTabs.autoUpdate = false;
        list.forEach(attrTabs::addTab);
        attrTabs.autoUpdate = true;
        attrTabs.invalidateSelect();
        Array<Tab> tabs = attrTabs.getTabs();
        Tab target = null;
        if(tabs.size > 0) target = tabs.first();
        attrTabs.switchedTab(target);
    }

    protected boolean generateWidget(List<AttrWidget> list, Material mtl, Attribute attr) {
        if(TextureAttribute.is(attr.type))
            return list.add(new TextureAttrWidget(mtl, (TextureAttribute) attr));
        if(ColorAttribute.is(attr.type))
            return list.add(new ColourAttrWidget(mtl, (ColorAttribute) attr));
//        if(ColorAttribute.is(attr.type))
//            return list.add(new ColorAttrWidget(mtl, (ColorAttribute)attr));
//        if(BlendingAttribute.is(attr.type))
//            return list.add(new BlendingAttrWidget(mtl, (BlendingAttribute)attr));
        return false;
    }

    @Override
    public void onMaterialComponentChange(MaterialComponentChangeEvent event) {
        onMaterialSelect(this.argMtl);
    }
}
