import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 22/06/2016.
 */
public class TestLauncher extends Game {

    @Override
    public void create() {
        VisUI.load();
        setScreen(new Loader(this));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Argent.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
        Argent.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void setScreen(Screen screen) {
        Gdx.graphics.setTitle(screen.getClass().getSimpleName());
        super.setScreen(screen);
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setWindowedMode(1600, 900);
        cfg.useOpenGL3(true, 4, 2);
        new Lwjgl3Application(new TestLauncher(), cfg);
    }

}
