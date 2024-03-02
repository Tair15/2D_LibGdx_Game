package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.EntityComponentSystem.Component.GameObjectComponent;
import com.mygdx.game.EntityComponentSystem.Component.RemoveComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;
import com.mygdx.game.Map.GameObjectType;
import com.mygdx.game.WorldContactListener;

public class PlayerCollisionSystem extends IteratingSystem implements WorldContactListener.PlayerCollisionListener {
    public PlayerCollisionSystem(Game2D context) {
        super(Family.all(RemoveComponent.class).get());
        context.getWorldContactListener().addPlayerCollisionListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        getEngine().removeEntity(entity);
    }

    @Override
    public void playerCollision(Entity player, Entity gameObject) {
        GameObjectComponent gameObjectComponent = EntityCSEngine.gameObjectCM.get(gameObject);

        if(gameObjectComponent.type == GameObjectType.SLIME){
            //remove slime and increase in in inventory
            gameObject.add(((EntityCSEngine)getEngine()).createComponent(RemoveComponent.class));
        }

    }
}
