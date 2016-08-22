package net.ncguy.argent.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;
import net.ncguy.argent.tween.accessor.ColorTweenAccessor;

/**
 * Created by Guy on 22/08/2016.
 */
public class TweenModule extends IModule {

    @Override
    public void init() {
        Argent.tween = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorTweenAccessor());
    }

    @Override
    public String moduleName() {
        return "Tween";
    }
}
