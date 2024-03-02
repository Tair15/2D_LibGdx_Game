package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.PlayerComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;
import com.mygdx.game.input.Game2DKeyInputListener;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;

public class PlayerMovementSystem extends IteratingSystem implements Game2DKeyInputListener {
    private int xFactor;
    private int yFactor;

    public PlayerMovementSystem(final Game2D context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        context.getInputManager().addInputListeners(this);
        xFactor = yFactor = 0;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
            final PlayerComponent playerComponent = EntityCSEngine.playerCM.get(entity);
            final B2DComponent b2DComponent = EntityCSEngine.b2dCM.get(entity);

            b2DComponent.body.applyLinearImpulse(
                    (xFactor * playerComponent.speed.x - b2DComponent.body.getLinearVelocity().x) * b2DComponent.body.getMass(),
                    (yFactor * playerComponent.speed.y - b2DComponent.body.getLinearVelocity().y) * b2DComponent.body.getMass(),
                    b2DComponent.body.getWorldCenter().x,
                    b2DComponent.body.getWorldCenter().y,
                    true
            );
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        switch (key) {
            case LEFT:
                xFactor = -1;
                break;
            case RIGHT:
                xFactor = 1;
                break;
            case UP:
                yFactor = 1;
                break;
            case DOWN:
                yFactor = -1;
                break;
            default:
                return;
        }

    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        switch (key) {
            case LEFT:
                xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 1 : 0;
                break;
            case RIGHT:
                xFactor = manager.isKeyPressed(GameKeys.LEFT) ? -1 : 0;
                break;
            case UP:
                yFactor = manager.isKeyPressed(GameKeys.DOWN) ? -1 : 0;
                break;
            case DOWN:
                yFactor = manager.isKeyPressed(GameKeys.UP) ? 1 : 0;
                break;
            default:
                return;
        }
    }
}
