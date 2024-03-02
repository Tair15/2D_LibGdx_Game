package com.mygdx.game.EntityComponentSystem;

import static com.mygdx.game.Game2D.BIT_GAME_OBJECT;
import static com.mygdx.game.Game2D.BIT_GROUND;
import static com.mygdx.game.Game2D.BIT_PLAYER;
import static com.mygdx.game.Game2D.UNIT_SCALE;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.EntityComponentSystem.Component.AnimationComponent;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.GameObjectComponent;
import com.mygdx.game.EntityComponentSystem.Component.PlayerComponent;
import com.mygdx.game.EntityComponentSystem.System.PlayerItemAnimationSystem;
import com.mygdx.game.Game2D;
import com.mygdx.game.Map.GameObject;
import com.mygdx.game.EntityComponentSystem.System.AnimationSystem;
import com.mygdx.game.EntityComponentSystem.System.PlayerAnimationSystem;
import com.mygdx.game.EntityComponentSystem.System.PlayerCameraSystem;
import com.mygdx.game.EntityComponentSystem.System.PlayerCollisionSystem;
import com.mygdx.game.EntityComponentSystem.System.PlayerMovementSystem;
import com.mygdx.game.UI.AnimationType;

public class EntityCSEngine extends PooledEngine {
    public static final ComponentMapper<PlayerComponent> playerCM =ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2dCM = ComponentMapper.getFor(B2DComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjectCM = ComponentMapper.getFor(GameObjectComponent.class);
    public static final ComponentMapper<AnimationComponent> animationCM = ComponentMapper.getFor(AnimationComponent.class);
    private final World world;
    private final Vector2 localPosition;
    private final Vector2 posBeforeRotation;
    private final Vector2 posAfterRotation;

    public EntityCSEngine(final Game2D context) {
        super();

        world = context.getWorld();
        this.localPosition = new Vector2(0,0);
        this.posBeforeRotation = new Vector2(0,0);
        this.posAfterRotation = new Vector2(0,0);

        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
        this.addSystem(new PlayerAnimationSystem(context));
        this.addSystem(new PlayerItemAnimationSystem(context));
        this.addSystem(new PlayerCollisionSystem(context));
    }
    public void createPlayer(final Vector2 playerSpawnLocation,final float height,final float width) {
        final Entity player = this.createEntity();

        //player component
        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);

        //box2d component
        Game2D.resetBodyFixtureDefinition();
        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        Game2D.BODY_DEF.position.set(playerSpawnLocation.x, playerSpawnLocation.y + height + 0.5f);
        Game2D.BODY_DEF.fixedRotation = true;
        Game2D.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        b2DComponent.body = world.createBody(Game2D.BODY_DEF);
        b2DComponent.body.setUserData(player);
        b2DComponent.width = width;
        b2DComponent.height = height;
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition());

        Game2D.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
        Game2D.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_GAME_OBJECT;
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox( 0.5f,  0.5f);
        Game2D.FIXTURE_DEF.shape = polygonShape;
        b2DComponent.body.createFixture(Game2D.FIXTURE_DEF);
        polygonShape.dispose();
        player.add(b2DComponent);

        //animation component
        AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        animationComponent.animationType = AnimationType.HERO_MOVE_RIGHT;
        animationComponent.width = 32*UNIT_SCALE;
        animationComponent.height = 32* UNIT_SCALE;

        player.add(animationComponent);

        this.addEntity(player);
    }

    public void createGameObject(GameObject gameObject) {
        final Entity gameObjectEntity = this.createEntity();

        //gameObject component
        final GameObjectComponent gameObjectComponent = this.createComponent(GameObjectComponent.class);
        gameObjectComponent.animationIndex = gameObject.getAnimationIndex();
        gameObjectComponent.type = gameObject.getType();
        gameObjectEntity.add(gameObjectComponent);

        //Animation component
        AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        animationComponent.animationType = null;
        animationComponent.width = gameObject.getWidth();
        animationComponent.height = gameObject.getHeight();
        gameObjectEntity.add(animationComponent);

        //box2d component
        Game2D.resetBodyFixtureDefinition();
        float halfW = gameObject.getWidth() * 0.5f;
        float halfH = gameObject.getHeight() * 0.5f;
        float angleRad = -gameObject.getRootDegree() * MathUtils.degreesToRadians;
        B2DComponent body = this.createComponent(B2DComponent.class);
        Game2D.BODY_DEF.type = BodyDef.BodyType.StaticBody;
        Game2D.BODY_DEF.position.set(gameObject.getPosition().x + halfW, gameObject.getPosition().y + halfH);
        body.body = world.createBody(Game2D.BODY_DEF);
        body.body.setUserData(gameObjectEntity);
        body.width = gameObject.getWidth();
        body.height = gameObject.getHeight();

        //save position before rotation = Tiled is rotating around bottom left corner
        localPosition.set(-halfW, -halfH);
        posBeforeRotation.set(body.body.getWorldPoint(localPosition));
        //rotate body
        body.body.setTransform(body.body.getPosition(), angleRad);
        //get position after rotation
        posAfterRotation.set(body.body.getWorldPoint(localPosition));
        //adjust pos to its original value b4 rotation
        body.body.setTransform(body.body.getPosition().add(posBeforeRotation).sub(posAfterRotation), angleRad);
        body.renderPosition.set(
                body.body.getPosition().x - animationComponent.width * 0.5f,
                body.body.getPosition().y - body.height * 0.5f);

        Game2D.FIXTURE_DEF.filter.categoryBits = BIT_GAME_OBJECT;
        Game2D.FIXTURE_DEF.filter.maskBits = BIT_PLAYER;
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(halfW, halfH);
        Game2D.FIXTURE_DEF.shape = polygonShape;
        body.body.createFixture(Game2D.FIXTURE_DEF);
        polygonShape.dispose();
        gameObjectEntity.add(body);

        this.addEntity(gameObjectEntity);
    }
}
