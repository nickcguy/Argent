package net.ncguy.argent.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

/**
 * Created by Guy on 13/07/2016.
 */
public class ArgentContactListener extends ContactListener {

    @Override
    public boolean onContactAdded(btManifoldPoint cp, btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1, int index1) {
        return true;
    }
}
