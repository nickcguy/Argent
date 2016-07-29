package net.ncguy.argent.editorOld.controller;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.utils.AppUtils;

import java.util.function.BiConsumer;

/**
 * Created by Guy on 22/07/2016.
 */
public enum ControllerType {
    NONE(Consumers::none),
    ORBIT(Consumers::orbit)
    ;
    ControllerType(BiConsumer<PerspectiveCamera, Vector3> behaviour) {
        this.behaviour = behaviour;
    }
    private BiConsumer<PerspectiveCamera, Vector3> behaviour;
    public void act(PerspectiveCamera camera, Vector3 point) {
        behaviour.accept(camera, point);
    }

    public static class Consumers {

        static void none(PerspectiveCamera camera, Vector3 point) {}

        static void orbit(PerspectiveCamera camera, Vector3 point) {
            AppUtils.Graphics.orbit(camera, point);
        }

    }
}
