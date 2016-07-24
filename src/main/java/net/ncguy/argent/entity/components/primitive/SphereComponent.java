package net.ncguy.argent.entity.components.primitive;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;

import java.util.List;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 24/07/2016.
 */
public class SphereComponent extends PrimitiveComponent {

    public Vector3 size;
    public Vector2 divisions;

    public SphereComponent() {
        super("Sphere");
        this.size = new Vector3(1, 1, 1);
        this.divisions = new Vector2(3, 3);
        applyModel();
    }

    @Override
    public Model buildModel() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        SphereShapeBuilder.build(mb.part("root", GL30.GL_TRIANGLES, Position | Normal | TextureCoordinates, new Material()),
                size.x, size.y, size.z, (int)divisions.x, (int)divisions.y);
        return mb.end();
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        attr(attrs, new Meta.Object("Width",  "Components|Sphere"), () -> size.x,             this::x, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        attr(attrs, new Meta.Object("Height", "Components|Sphere"), () -> size.y,             this::y, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        attr(attrs, new Meta.Object("Depth",  "Components|Sphere"), () -> size.z,             this::z, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        attr(attrs, new Meta.Object("Divisions U",  "Components|Sphere"), () -> divisions.x,  this::u, ConfigControl.NUMBERSELECTOR, this::divisionsTunnel);
        attr(attrs, new Meta.Object("Divisions V",  "Components|Sphere"), () -> divisions.y,  this::v, ConfigControl.NUMBERSELECTOR, this::divisionsTunnel);
        super.getConfigurableAttributes(attrs);
    }

    protected float divisionsTunnel(String s) {
        float f = MathUtils.floor(positiveTunnel(s));
        if(f < 3) f = 3;
        return f;
    }

    protected void x(float x) {
        this.size.x = x;
        applyModel();
    }
    protected void y(float y) {
        this.size.y = y;
        applyModel();
    }
    protected void z(float z) {
        this.size.z = z;
        applyModel();
    }
    protected void u(float u) {
        this.divisions.x = u;
        applyModel();
    }
    protected void v(float v) {
        this.divisions.y = v;
        applyModel();
    }

}
