package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import net.ncguy.argent.entity.attributes.PickerIDAttribute;

/**
 * Created by Guy on 21/07/2016.
 */
public class AppUtils {

    public static class Stage2d {
        public static void keepWithinParent(Actor actor) {
            if(actor.hasParent()) keepWithinActor(actor, actor.getParent());
        }

        public static void keepWithinStage(Actor actor) {
            Stage stage = actor.getStage();
            if(stage != null) {
                Actor dummy = new Actor();
                dummy.setPosition(0, 0);
                dummy.setSize(stage.getWidth(), stage.getHeight());
                keepWithinActor(actor, dummy);
            }
        }

        public static void keepWithinActor(Actor actor, Actor grp) {
            float x = actor.getX(), y = actor.getY();
            float w = actor.getWidth(), h = actor.getHeight();

            if(x < grp.getX()) x = grp.getX();
            if(y < grp.getY()) y = grp.getY();
            if(x+w > grp.getX()+grp.getWidth()) x = grp.getWidth()-w;
            if(y+h > grp.getY()+grp.getHeight()) y = grp.getHeight()-h;

            actor.setBounds(Math.round(x), Math.round(y), Math.round(w), Math.round(h));
        }
    }

    public static class Input {

        public static Vector2 getPackedCursorPos() {
            Vector2 pos = new Vector2();
            pos.x = Gdx.input.getX();
            pos.y = Gdx.graphics.getHeight() - Gdx.input.getY();
            return pos;
        }

        /**
         * Adds the provided input processor to the active processor<br/>
         * If the active processor is not {@link InputMultiplexer} then it will be converted to one
         * @param processor
         */
        public static void addInputProcessor(InputProcessor processor) {
            InputProcessor current = Gdx.input.getInputProcessor();
            if(current != null) {
                if (current instanceof InputMultiplexer) {
                    ((InputMultiplexer) current).addProcessor(processor);
                } else {
                    InputMultiplexer multiplexer = new InputMultiplexer(current, processor);
                    Gdx.input.setInputProcessor(multiplexer);
                }
            }else{
                InputMultiplexer multiplexer = new InputMultiplexer(processor);
                Gdx.input.setInputProcessor(multiplexer);
            }
        }

        /**
         * Removes the provided input processor from the active processor<br/>
         * The active processor must be a {@link InputMultiplexer} for this method to run
         * @param processor
         */
        public static void removeInputProcessor(InputProcessor processor) {
            InputProcessor current = Gdx.input.getInputProcessor();
            if(current != null) {
                if (current instanceof InputMultiplexer) {
                    ((InputMultiplexer) current).removeProcessor(processor);
                }
            }
        }

    }

    public static class Graphics {

        public static Color randomColour() {
            return new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 1.0f);
        }

        public static Vector3 getRightVector(Camera camera) {
            return getRightVector(camera.direction.cpy(), camera.up.cpy());
        }

        public static Vector3 getRightVector(Vector3 forward, Vector3 up) {
            return forward.crs(up);
        }

        public static Vector3 getFlatForwardVector(Camera camera) {
            return getFlatForwardVector(camera.direction);
        }

        public static Vector3 getFlatForwardVector(Vector3 forward) {
            Vector3 flat = forward.cpy();
            flat.y = 0;
            return flat;
        }

        public static Vector3 getFlatRightVector(Camera camera) {
            return getRightVector(getFlatForwardVector(camera), camera.up);
        }

        public static void orbit(PerspectiveCamera camera, Vector3 point) {
            orbit(camera, point, 1);
        }

        public static void orbit(PerspectiveCamera camera, Vector3 point, float speed) {
            float y = camera.position.y;
            camera.position.y = point.y;

            camera.rotateAround(point, new Vector3(1, 0, 0), speed);
            camera.position.y = y;
            camera.lookAt(point);
            camera.update(true);
        }

        public static void getEyeRay(Camera camera, float x, float y, Vector3 result) {
            Vector3 tmp0 = new Vector3();
            Quaternion tmp3 = new Quaternion();
            tmp3.set(x, y, 0, 1);
            tmp3.mul(1f / tmp3.w);
            tmp0.set(tmp3.x, tmp3.y, tmp3.z);
            if(result == null) return;
            result.set(tmp0);
            result.sub(camera.position);
        }

        public static Matrix4 getInverseProjectionViewMatrix(Camera camera) {
            Matrix4 mat = camera.combined.cpy();
            mat.mul(getViewMatrix(camera));
            mat.inv();
            return mat;
        }

        public static Matrix4 getViewMatrix(Camera camera) {
            Vector3 tmp0 = new Vector3();
            Vector3 tmp1 = new Vector3();
            Vector3 tmp2 = new Vector3();
            tmp0.set(camera.direction);
            tmp0.nor();
            tmp1.set(camera.up);
            tmp1.nor();
		/* right = direction x up */
            tmp1.set(tmp0.cpy().crs(tmp1));
		/* up = right x direction */
            tmp2.set(tmp1.cpy().crs(tmp0));
            Matrix4 m = new Matrix4();
            m.getValues()[Matrix4.M00] = tmp1.x;
            m.getValues()[Matrix4.M01] = tmp1.y;
            m.getValues()[Matrix4.M02] = tmp1.z;
            m.getValues()[Matrix4.M03] = -tmp1.x * camera.position.x - tmp1.y * camera.position.y - tmp1.z * camera.position.z;
            m.getValues()[Matrix4.M10] = tmp2.x;
            m.getValues()[Matrix4.M11] = tmp2.y;
            m.getValues()[Matrix4.M12] = tmp2.z;
            m.getValues()[Matrix4.M13] = -tmp2.x * camera.position.x - tmp2.y * camera.position.y - tmp2.z * camera.position.z;
            m.getValues()[Matrix4.M20] = -tmp0.x;
            m.getValues()[Matrix4.M21] = -tmp0.y;
            m.getValues()[Matrix4.M22] = -tmp0.z;
            m.getValues()[Matrix4.M23] = tmp0.x * camera.position.x + tmp0.y * camera.position.y + tmp0.z * camera.position.z;
            m.getValues()[Matrix4.M30] = 0.0f;
            m.getValues()[Matrix4.M31] = 0.0f;
            m.getValues()[Matrix4.M32] = 0.0f;
            m.getValues()[Matrix4.M33] = 1.0f;
            return m;
        }
    }

    public static class Shader {

        public static final String shaderFormat = "assets/shaders/%s.%s";

        public static GeometryShaderProgram loadGeometryShader(String prefix) {
            String vertPath = String.format(shaderFormat, prefix, "vert");
            String geomPath = String.format(shaderFormat, "geometry/world", "geom");
            String fragPath = String.format(shaderFormat, prefix, "frag");
            System.out.println(prefix);
            GeometryShaderProgram prg = new GeometryShaderProgram(Gdx.files.internal(vertPath), Gdx.files.internal(geomPath), Gdx.files.internal(fragPath));
            System.out.println(prg.getLog());
            if(!prg.isCompiled()) return null;
            return prg;
        }

        public static ShaderProgram loadShader(String prefix) {
            String vertPath = String.format(shaderFormat, prefix, "vert");
            String fragPath = String.format(shaderFormat, prefix, "frag");
            System.out.println(prefix);
            return compileShader(Gdx.files.internal(vertPath), Gdx.files.internal(fragPath));
        }
        public static ShaderProgram compileShader(FileHandle vertHandle, FileHandle fragHandle) {
            return compileShader(vertHandle.readString(), fragHandle.readString());
        }
        public static ShaderProgram compileShader(String vert, String frag) {
            ShaderProgram.pedantic = false;
            ShaderProgram prg = new ShaderProgram(vert, frag);
            System.out.println(prg.getLog());
            if(!prg.isCompiled()) return null;
            return prg;
        }
        public static Color bindPickerAttribute(Attributes combinedAttributes) {
            if(combinedAttributes.has(PickerIDAttribute.Type)) {
                return ((PickerIDAttribute) (combinedAttributes.get(PickerIDAttribute.Type))).colour;
            }else{
                return Color.WHITE;
            }
        }

        public static int bindTextureAttribute(BaseShader shader, Attributes combinedAttributes, long type) {
            if(combinedAttributes.has(type)) {
                return shader.context.textureBinder.bind(((TextureAttribute) (combinedAttributes
                        .get(type))).textureDescription);
            }else{
                TextureDescriptor<Texture> descriptor = new TextureDescriptor<>(TextureCache.white(), Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                return shader.context.textureBinder.bind(descriptor);
            }
        }

    }

    public static class GL {

        public static float getTime() {
            return (float) -(Math.PI/2);
        }

    }

    public static class General {

        public static <T> boolean arrayContains(T[] arr, T obj) {
            for (T t : arr) if(obj.equals(t)) return true;
            return false;
        }

    }

}
