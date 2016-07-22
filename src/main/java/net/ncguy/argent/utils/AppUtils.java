package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

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
    }

}
