package net.ncguy;

import net.ncguy.argent.ArgentGame;
import net.ncguy.screen.GameScreen;

/**
 * Created by Guy on 15/07/2016.
 */
public class ArgentSample extends ArgentGame {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }

}
