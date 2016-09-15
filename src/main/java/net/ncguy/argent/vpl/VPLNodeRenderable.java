package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import net.ncguy.argent.ui.Icons;
import net.ncguy.argent.utils.gdx.Renderer2D;

import java.util.List;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLNodeRenderable {

    NinePatchDrawable nodeBG;
    NinePatchDrawable nodeSelected;
    Drawable execPin;
    Drawable execPinConnected;
    Drawable pin;
    Drawable pinConnected;
    NinePatchDrawable title;
    NinePatchDrawable titleGloss;
    NinePatchDrawable titleHighlight;
    NinePatchDrawable invalidateIcon;

    private static VPLNodeRenderable instance;
    public static VPLNodeRenderable instance() {
        if (instance == null)
            instance = new VPLNodeRenderable();
        return instance;
    }

    private VPLNodeRenderable() {
        nodeBG = new NinePatchDrawable(Icons.Node.NODE_BODY.patch());
        nodeSelected = new NinePatchDrawable(Icons.Node.NODE_SELECTED.patch());
        execPin = Icons.Node.PIN_EXEC.drawable();
        execPinConnected = Icons.Node.PIN_EXEC_CONNECTED.drawable();
        pin = Icons.Node.PIN.drawable();
        pinConnected = Icons.Node.PIN_CONNECTED.drawable();
        title = new NinePatchDrawable(Icons.Node.NODE_TITLE.patch());
        titleGloss = new NinePatchDrawable(Icons.Node.NODE_TITLE_GLOSS.patch());
        titleHighlight = new NinePatchDrawable(Icons.Node.NODE_TITLE_HIGHLIGHT.patch());
        invalidateIcon = new NinePatchDrawable(Icons.Node.NODE_INVALIDATE.patch());
    }

    public void render(Batch batch, VPLNode node) {
//        getX(), getY(), getWidth(), getHeight(), headerTable.getHeight()
//        render(batch, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getHeaderHeight(), node.isSelected(), node.titleColour);

        float x = node.getX();
        float y = node.getY();
        float w = node.getWidth();
        float h = node.getHeight();

        if(node.isSelected()) {
            float offset = 3;
            float selX = x - nodeSelected.getLeftWidth();
            float selY = y - nodeSelected.getBottomHeight();
            float selW = w + nodeSelected.getLeftWidth() + nodeSelected.getRightWidth();
            float selH = h + nodeSelected.getBottomHeight() + nodeSelected.getTopHeight();
            nodeSelected.draw(batch, selX+offset, selY+offset, selW-(offset*2), selH-(offset*2));
        }

        float titleHeight = node.getHeaderHeight();

        nodeBG.draw(batch, x, y, w, h);
        batch.setColor(node.titleColour);
        title.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        batch.setColor(Color.WHITE);

        if(node.continuous) {
            float iconSize = titleHeight-8;
            invalidateIcon.draw(batch, x + (w - (iconSize + 4)), y + (h - (iconSize + 4)), iconSize, iconSize);
        }

//        titleGloss.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        titleHighlight.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
    }

    public void render(Batch batch, float x, float y, float w, float h) {
        render(batch, x, y, w, h, 24);
    }

    public void render(Batch batch, float x, float y, float w, float h, float titleHeight) {
        render(batch, x, y, w, h, titleHeight, false);
    }

    public void render(Batch batch, float x, float y, float w, float h, boolean isSelected) {
        render(batch, x, y, w, h, 24, isSelected);
    }

    public void render(Batch batch, float x, float y, float w, float h, float titleHeight, boolean isSelected) {
        render(batch, x, y, w, h, titleHeight, isSelected, Color.WHITE);
    }

    public void render(Batch batch, float x, float y, float w, float h, float titleHeight, boolean isSelected, Color titleColour) {
        if(isSelected) {
            float offset = 3;
            float selX = x - nodeSelected.getLeftWidth();
            float selY = y - nodeSelected.getBottomHeight();
            float selW = w + nodeSelected.getLeftWidth() + nodeSelected.getRightWidth();
            float selH = h + nodeSelected.getBottomHeight() + nodeSelected.getTopHeight();
            nodeSelected.draw(batch, selX+offset, selY+offset, selW-(offset*2), selH-(offset*2));
        }
        nodeBG.draw(batch, x, y, w, h);
        batch.setColor(titleColour);
        title.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        batch.setColor(Color.WHITE);
        titleGloss.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        titleHighlight.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
    }

    public void renderPin(Batch batch, VPLPin vplPin) {
        Drawable drawable;
        if(vplPin.is(VPLPin.Types.EXEC)) {
            if(vplPin.isConnected())
                drawable = execPinConnected;
            else drawable = execPin;
        }else{
            if(vplPin.isConnected())
                drawable = pinConnected;
            else drawable = pin;
        }
        drawable.draw(batch, vplPin.getX(), vplPin.getY(), vplPin.getWidth(), vplPin.getHeight());
    }

    public void renderSplines(Batch batch, VPLGraph graph) {
        for (VPLNode node : graph.nodes)
            renderSplines(batch, node);
    }
    public void renderSplines(Batch batch, VPLNode<?> node) {
        for (VPLPin pin : node.pinSet)
            renderSplines(batch, pin);
    }
    public void renderSplines(Batch batch, VPLPin pin) {
        if(pin.is(VPLPin.Types.INPUT)) return;
        List<VPLPin> targets = pin.connectedPins;
        for (VPLPin target : targets) {
            renderTweenSpline(batch, VPLPin.splineFidelity, pin.getColour(), target.getColour(), normalizePoints(pin, defaultPoints(pin.getNormalPosition(), target.getNormalPosition())));
//            renderTweenSpline(batch, VPLPin.splineFidelity, pin.getDiffuse(), target.getDiffuse(), defaultPoints(pin.getPosition(), target.getPosition()));
        }
    }

    public Vector2[] defaultPoints(Vector2 start, Vector2 end) {
        return defaultPoints(start, end, Interpolation.linear, Interpolation.fade);
    }

    public Vector2[] defaultPoints(Vector2 start, Vector2 end, Interpolation i) {
        return defaultPoints(start, end, i, i);
    }
    public Vector2[] defaultPoints(Vector2 start, Vector2 end, Interpolation x, Interpolation y) {
        Vector2[] points = new Vector2[13];
        points[0] = new Vector2(start);
        points[1] = new Vector2(start);
        for(int j = 2; j <= 10; j++) {
            float k = ((float)j-2) / 8f;
            Vector2 vec = new Vector2();
            vec.x = x.apply(start.x, end.x, k);
            vec.y = y.apply(start.y, end.y, k);
            points[j] = vec;
        }
        points[11] = new Vector2(end);
        points[12] = new Vector2(end);
        return points;
    }

    public Vector2 normalizePoint(Actor anchor, Vector2 point) {
        return anchor.localToStageCoordinates(point.cpy());
    }
    public Vector2[] normalizePoints(Actor anchor, Vector2[] points) {
        for (Vector2 point : points)
            normalizePoint(anchor, point);
        return points;
    }

    public Vector2[] reticulateSpline(int splineFidelity, Vector2... points) {
        int fidelity = splineFidelity * (points.length+1);
        Vector2[] output = new Vector2[fidelity];
        Path<Vector2> catmull = new CatmullRomSpline<>(points, false);
        for(int i = 0; i < fidelity; ++i) {
            output[i] = new Vector2();
            catmull.valueAt(output[i], (float)i / ((float)fidelity - 1));
        }
        return output;
    }
    public void renderSpline(Batch batch, int splineFidelity, Color col, Vector2... points) {
        Vector2[] splinePoints = reticulateSpline(splineFidelity, points);
        if(splinePoints == null) return;
        if(splinePoints.length < 1) return;
        batch.end();
        ShapeRenderer renderer = Renderer2D.instance().getRenderer();
        renderer.begin(ShapeRenderer.ShapeType.Point);
        renderer.setColor(col);
        for (Vector2 point : splinePoints)
            renderer.point(point.x, point.y, 0);
        renderer.setColor(Color.WHITE);
        renderer.end();
        batch.begin();
    }

    public void renderTweenSpline(Batch batch, int splineFidelity, Color startCol, Color endCol, Vector2... points) {
        renderTweenSpline(batch, splineFidelity, startCol, endCol, Interpolation.linear, points);
    }
    public void renderTweenSpline(Batch batch, int splineFidelity, Color startCol, Color endCol, Interpolation interp, Vector2... points) {
        Vector2[] splinePoints = reticulateSpline(splineFidelity, points);
        if(splinePoints == null) return;
        if(splinePoints.length < 1) return;
        batch.end();
        ShapeRenderer renderer = Renderer2D.instance().getRenderer();
        renderer.begin(ShapeRenderer.ShapeType.Point);
        for (int i = 0; i < splinePoints.length; i++) {
            float p = (float)i / (float)splinePoints.length;
            renderer.setColor(tweenCol(startCol, endCol, interp, p));
            Vector2 point = splinePoints[i];
            renderer.point(point.x, point.y, 0);
        }
        renderer.setColor(Color.WHITE);
        renderer.end();
        batch.begin();
        batch.setColor(Color.WHITE);
    }

    private Color tweenCol(Color a, Color b, Interpolation i, float p) {
        Color c = new Color();
        c.r = i.apply(a.r, b.r, p);
        c.g = i.apply(a.g, b.g, p);
        c.b = i.apply(a.b, b.b, p);
        c.a = i.apply(a.a, b.a, p);
        return c;
    }
}
