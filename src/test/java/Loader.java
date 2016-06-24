import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 22/06/2016.
 */
public class Loader implements Screen {

    TestLauncher game;

    public Loader(TestLauncher game) {
        this.game = game;
    }

    @Override
    public void show() {
        Argent.initStandard();
        Argent.content.setOnFinish(manager -> {
            System.out.println("Loaded Assets:");
            Argent.content.assetMap().forEach((k, v) -> System.out.printf("\t%s: %s\n", k, v));
            game.setScreen(new MenuScreen(game));
        });
        FileHandle assetHandle = Gdx.files.internal("assets");
        System.out.println(assetHandle.file().getAbsolutePath());
        Argent.content.addDirectoryRoot(assetHandle.file(), Texture.class, "png", "jpg");
        Argent.content.addDirectoryRoot(assetHandle.file(), Model.class, "g3db");
        Argent.content.start();
    }

    @Override
    public void render(float delta) {
        Argent.content.update();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
