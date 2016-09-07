package net.ncguy.argent.vpl.nodes.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.utils.TextureCache;
import net.ncguy.argent.vpl.*;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.WidgetNode;
import net.ncguy.argent.vpl.struct.IdentifierObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static net.ncguy.argent.vpl.VPLPin.Types.INPUT;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 28/08/2016.
 */
@NodeData(value = "Texture Sampler", outPins = 5, execIn = false, execOut = false, tags = "shader",
extras = {"file", "assets/shaders/graph/fragments/textureSample.frag"})
public class TextureNode extends WidgetNode<Object> implements IShaderNode {

    Image textureZone;
    AssetSelector<ArgTexture> textureAssetSelector;
    ArgTexture selected;
    Image texelPreview;
    VPLPin uvPin;

    public TextureNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    public TextureNode(VPLGraph graph) {
        super(graph);
    }

    @Override
    protected void buildInput() {
        uvPin = addPin(inputTable, Vector2.class, "UV", INPUT);

        textureAssetSelector = new AssetSelector<ArgTexture>() {
            @Override
            public List<ArgTexture> getAssets() {
                return manager.current().textures();
            }
        };
        textureAssetSelector.setChangeListener(item -> selectTexture(item.value));

        textureZone = new Image();
        if(selected != null)
            textureZone.setDrawable(selected.icon());
        textureZone.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                textureAssetSelector.show(getStage(), pos);
            }
        });

        texelPreview = new Image(TextureCache.white());

        uvPin.addPinListener(new VPLPinListener() {
            @Override
            public void connected(VPLPin other) {

            }

            @Override
            public void disconnected(VPLPin other) {

            }
        });


        Table previewTable = new Table(VisUI.getSkin());
        previewTable.add(textureZone).size(64).padRight(2);
        previewTable.add(texelPreview).size(64).row();


        Cell cell = addElement(inputTable, previewTable);
        cell.colspan(2);
        cell.size(130);
        setDebug(false, true);
        textureZone.setDebug(true);
    }

    public void selectTexture(ArgTexture tex) {
        selected = tex;
        textureZone.setDrawable(tex.icon());
    }

    @Override
    public void act(float delta) {
//        setTexelPreviewRegion();
        super.act(delta);
    }

    @Override
    public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Color.class, "Colour", OUTPUT);
        addPin(outputTable, float.class, "Red", OUTPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.RED);
        addPin(outputTable, float.class, "Green", OUTPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.GREEN);
        addPin(outputTable, float.class, "Blue", OUTPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.BLUE);
        addPin(outputTable, float.class, "Alpha", OUTPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.ALPHA);
    }

    @Override
    protected void discernType() {
        discernType(Object.class, 3);
    }

    @Override
    public IdentifierObject fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return null;
    }

    public void setTexelPreviewRegion() {
        if(uvPin == null) return;
        VPLNode node = uvPin.getConnectedNode(0);
        if(node == null || node == this) return;
        try {
            Object o = node.fetchData(TextureNode.this);
            if(o instanceof Vector2)
                setTexelPreviewRegion((Vector2) o);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setTexelPreviewRegion(Vector2 pos) {
        pos.x %= 1;
        pos.y %= 1;
        TextureData data = selected.texture.getTextureData();
        if(!data.isPrepared()) data.prepare();
        int x = (int) Math.floor(data.getWidth()  * pos.x);
        int y = (int) Math.floor(data.getHeight() * pos.y);
        Pixmap map = data.consumePixmap();
        int pixel = map.getPixel(x, y);
        Color.rgba8888ToColor(texelPreview.getColor(), pixel);
    }

    @Override
    public void writeToFile(Kryo kryo, Output output) {
        kryo.writeObject(output, selected);
    }

    @Override
    public void readFromFile(Kryo kryo, Input input) {
        selectTexture(kryo.readObject(input, ArgTexture.class));
    }

    // Shader Stuff

    public static int globalTexUnit = 1;

    int texUnit = 0;
    boolean fragmentUsed = false;

    @Override
    public void resetStaticCache() {
        Log.info("Reset static cache");
        globalTexUnit = 1;
    }

    @Override
    public void resetCache() {
        Log.info("Reset instance cache");
        texUnit = globalTexUnit++;
        fragmentUsed = false;
    }

    @Override
    public String getUniforms() {
        return "uniform sampler2D u_texture"+texUnit+";\n" +
                "vec4 texture"+texUnit+"_colour;\n" +
                "vec2 texture"+texUnit+"_uv;\n";
    }

    @Override
    public String getFragment() {
        if(fragmentUsed) return "";
        fragmentUsed = true;
        IShaderNode node0 = getNodePacker(0);
        if(node0 != null) {
            String pre = node0.getFragment();
            String var = node0.getVariable(this);
            return String.format("%stexture%s_colour = texture(u_texture%s, %s);", pre, texUnit, texUnit, var);
        }
        return "Error, this line will cause\na crash, this crash is caused by "+this.getClass().getCanonicalName();
    }

    @Override
    public String getVariable(int pinId) {
        String swizzle = "";
        switch(pinId) {
            case 1: swizzle = ".r";     break;
            case 2: swizzle = ".g";     break;
            case 3: swizzle = ".b";     break;
            case 4: swizzle = ".a";     break;
        }
        return "texture"+texUnit+"_colour"+swizzle+"";
    }

    @Override
    public boolean singleUseFragment() {
        return true;
    }

    @Override
    public void bind(ShaderProgram program) {
        if(selected == null) return;
        selected.texture.bind(texUnit);
        String uniform = "u_texture"+texUnit;
        program.setUniformi(uniform, texUnit);
    }
}
