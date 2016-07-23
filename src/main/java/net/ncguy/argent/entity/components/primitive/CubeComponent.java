package net.ncguy.argent.entity.components.primitive;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;

import java.util.List;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 23/07/2016.
 */
public class CubeComponent extends PrimitiveComponent {

    public Vector3 size;

    public CubeComponent() {
        super("Cube");
        this.size = new Vector3(1, 1, 1);
        applyModel();
    }

    @Override
    public Model buildModel() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        BoxShapeBuilder.build(mb.part("root", GL30.GL_TRIANGLES, Position | Normal | TextureCoordinates, new Material()),
                size.x, size.y, size.z);
        return mb.end();
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        attr(attrs, new Meta.Object("Width",  "Components|Cube"), () -> size.x, this::x, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        attr(attrs, new Meta.Object("Height", "Components|Cube"), () -> size.y, this::y, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        attr(attrs, new Meta.Object("Depth",  "Components|Cube"), () -> size.z, this::z, ConfigControl.NUMBERSELECTOR, this::positiveTunnel);
        super.getConfigurableAttributes(attrs);
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
}

