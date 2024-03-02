package com.mygdx.game.EntityComponentSystem.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.EntityComponentSystem.Component.AnimationComponent;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.PlayerComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;
import com.mygdx.game.UI.AnimationType;

public class PlayerItemAnimationSystem extends IteratingSystem {
    private final Animation<Sprite> aniLeft;
    private final Animation<Sprite> aniRight;
    private final Animation<Sprite> aniUp;
    private final Animation<Sprite> aniDown;
    public PlayerItemAnimationSystem(Game2D context) {
        super(Family.all(PlayerComponent.class, AnimationComponent.class).get());

        // create Item animations
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("items/sword.atlas"));
        TextureAtlas.AtlasRegion atlasRegion = atlas.findRegion("sword");

        final TextureRegion[][] textureRegions = atlasRegion.split(32, 32);
        aniUp = new Animation<>(0.05f, getKeyFrames(textureRegions[0]), Animation.PlayMode.LOOP);
        aniLeft = new Animation<>(0.05f, getKeyFrames(textureRegions[0]), Animation.PlayMode.LOOP);
        aniDown = new Animation<>(0.05f, getKeyFrames(textureRegions[0]), Animation.PlayMode.LOOP);
        aniRight = new Animation<>(0.05f, getKeyFrames(textureRegions[0]), Animation.PlayMode.LOOP);
    }


    private Array<Sprite> getKeyFrames(final TextureRegion[] textureRegions) {
        final Array<Sprite> keyFrames = new Array<>();

        for (final TextureRegion region : textureRegions) {
            keyFrames.add(new Sprite(region));
        }

        return keyFrames;
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
