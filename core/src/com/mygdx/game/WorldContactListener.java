package com.mygdx.game;

import static com.mygdx.game.Game2D.BIT_GAME_OBJECT;
import static com.mygdx.game.Game2D.BIT_PLAYER;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class WorldContactListener implements ContactListener {
    private final Array<PlayerCollisionListener> listeners;

    public WorldContactListener(){
        listeners = new Array<>();
    }

    public void addPlayerCollisionListener(final PlayerCollisionListener listener){
        listeners.add(listener);
    }
    @Override
    public void beginContact(Contact contact) {
//        Fixture A = contact.getFixtureA();
//        Fixture B = contact.getFixtureB();
//
//        Gdx.app.debug("CONTACT","BEGIN "+ A.getBody().getUserData()+" "+A.isSensor());
//        Gdx.app.debug("CONTACT","BEGIN "+ B.getBody().getUserData()+" "+B.isSensor());
//

        Entity player = null;
        Entity gameObj = null;

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        int catFixA = contact.getFixtureA().getFilterData().categoryBits;
        int catFixB = contact.getFixtureB().getFilterData().categoryBits;

        if ((catFixA & BIT_PLAYER) == BIT_PLAYER) {
            player = (Entity) bodyA.getUserData();
        } else if ((catFixB & BIT_PLAYER) == BIT_PLAYER) {
            player = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        if ((catFixA & BIT_GAME_OBJECT) == BIT_GAME_OBJECT) {
            gameObj = (Entity) bodyA.getUserData();
        } else if ((catFixB & BIT_GAME_OBJECT) == BIT_GAME_OBJECT) {
            gameObj = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        // Check if both player and gameObj are non-null before invoking listeners
        if (player != null && gameObj != null) {
            System.out.println("Collision detected between player and game object");
            for (final PlayerCollisionListener listener : listeners) {
                listener.playerCollision(player, gameObj);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    public interface PlayerCollisionListener{
        void playerCollision(final Entity player,final Entity gameObject);
    }
}
