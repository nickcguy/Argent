package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 24/06/2016.
 */
public class NotificationContainer extends Group {

    private List<NotificationActor> actorList;

    public NotificationContainer() {
        this.actorList = new ArrayList<>();
    }

    @Override
    public void addActor(Actor actor) {
        if(!(actor instanceof NotificationActor)) return;
        super.addActor(actor);
        this.actorList.add((NotificationActor)actor);
        invalidate(true);
    }

    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        if(this.actorList.contains(actor))
            this.actorList.remove(actor);
        invalidate(true);
        return super.removeActor(actor, unfocus);
    }

    public int padding = 4;

    public void invalidate(boolean tween) {
        padding = 4;
        NotificationActor[] actors = new NotificationActor[actorList.size()];
        int index = 0;
        for (NotificationActor actor : actorList) actors[index++] = actor;
        int len = actors.length;
        System.out.println(getHeight());
        for(int i = 0; i < len; i++) {
            NotificationActor n = actors[i];
            float targetY = getHeight()-((n.getHeight()+padding)*(i+1));
            n.setX(0);
            if(tween) n.addAction(Actions.moveTo(0, targetY, 0.3f, Interpolation.fade));
            else n.setY(targetY);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
