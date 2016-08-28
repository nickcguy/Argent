package net.ncguy.argent;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import net.ncguy.argent.injector.InjectionStore;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class ArgentGame extends Game {

    public ArgentGame() {}

    @Override
    public void create() {
        Argent.loadDefaultModules();
        try {
            InjectionStore.setGlobal(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setScreenNow(Screen screen) {
        Argent.loadModule(new CoreModule());
        super.setScreen(screen);
    }

    @Override
    public void setScreen(Screen screen) {
        Gdx.app.postRunnable(() -> setScreenNow(screen));
    }

    @Override
    public void render() {
        super.render();
        if(Argent.tween != null)
            Argent.tween.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Argent.onResize(width, height);
    }


}
