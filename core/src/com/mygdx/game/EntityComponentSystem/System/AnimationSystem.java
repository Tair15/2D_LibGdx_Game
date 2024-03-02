package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.EntityComponentSystem.Component.AnimationComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;

public class AnimationSystem extends IteratingSystem {
    public AnimationSystem(Game2D context) {
        super(Family.all(AnimationComponent.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final AnimationComponent animationComponent = EntityCSEngine.animationCM.get(entity);
//        if (animationComponent.animationType != null) {
            animationComponent.animationTime += deltaTime;
//        }
    }
}
