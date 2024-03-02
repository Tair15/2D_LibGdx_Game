package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.PlayerComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;

public class PlayerCameraSystem extends IteratingSystem {
    private final OrthographicCamera gameCamera;
    public PlayerCameraSystem(final Game2D context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        gameCamera = context.getGameCamera();
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        gameCamera.position.set(EntityCSEngine.b2dCM.get(entity).renderPosition,0);

    }
}
