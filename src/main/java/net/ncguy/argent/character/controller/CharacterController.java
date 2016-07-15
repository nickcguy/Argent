package net.ncguy.argent.character.controller;

import net.ncguy.argent.character.Character;

/**
 * Created by Guy on 13/07/2016.
 */
public class CharacterController {

    protected Character character;

    public CharacterController(Character character) {
        possess(character);
    }

    public void possess(Character character) {
        if(this.character != null) unPossess();
        if(character == null) return;
        this.character = character;
        this.character.onPossess(this);
    }

    public void unPossess() {
        if(this.character == null) return;
        this.character.onUnPossess();
        this.character = null;
    }

    public void update() {}


}
