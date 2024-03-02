package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.EntityComponentSystem.Component.AnimationComponent;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.PlayerComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;

import com.mygdx.game.UI.AnimationType;

public class PlayerAnimationSystem extends IteratingSystem {
    public PlayerAnimationSystem(Game2D context) {
        super(Family.all(AnimationComponent.class, PlayerComponent.class, B2DComponent.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2DComponent b2DComponent = EntityCSEngine.b2dCM.get(entity);
        final AnimationComponent animationComponent = EntityCSEngine.animationCM.get(entity);

        if (b2DComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
            animationComponent.animationTime = 0;
        } else if (b2DComponent.body.getLinearVelocity().x > 0) {
            animationComponent.animationType = AnimationType.HERO_MOVE_RIGHT;
        } else if (b2DComponent.body.getLinearVelocity().x < 0) {
            animationComponent.animationType = AnimationType.HERO_MOVE_LEFT;
        } else if (b2DComponent.body.getLinearVelocity().y > 0) {
            animationComponent.animationType = AnimationType.HERO_MOVE_UP;
        } else if (b2DComponent.body.getLinearVelocity().y < 0) {
            animationComponent.animationType = AnimationType.HERO_MOVE_DOWN;
        }
    }
}
