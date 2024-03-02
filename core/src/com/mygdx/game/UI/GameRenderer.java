package com.mygdx.game.UI;

import static com.mygdx.game.Game2D.UNIT_SCALE;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.EntityComponentSystem.Component.AnimationComponent;
import com.mygdx.game.EntityComponentSystem.Component.B2DComponent;
import com.mygdx.game.EntityComponentSystem.Component.GameObjectComponent;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MapListener;

import java.util.EnumMap;

public class GameRenderer implements Disposable, MapListener {
    public static final String TAG = GameRenderer.class.getSimpleName();

    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;
    private final EnumMap<AnimationType, Animation<Sprite>> animationCache;


    private final ImmutableArray<Entity> animatedEntities;
    private final ImmutableArray<Entity> gameObjectEntities;


    private final OrthogonalTiledMapRenderer mapRenderer;

    private final GLProfiler profiler;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World world;
    private final Array<TiledMapTileLayer> tiledMapLayers;
    private final ObjectMap<String, TextureRegion[][]> regionCache;
    private IntMap<Animation<Sprite>> mapAnimations;


    public GameRenderer(final Game2D context) {
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getGameCamera();
        spriteBatch = context.getSpriteBatch();

        animationCache = new EnumMap<>(AnimationType.class);
        regionCache = new ObjectMap<>();

        gameObjectEntities = context.getEntityCSEngine().getEntitiesFor(Family.all(GameObjectComponent.class, B2DComponent.class, AnimationComponent.class).get());
        animatedEntities = context.getEntityCSEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).exclude(GameObjectComponent.class).get());


        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, spriteBatch);
        context.getMapManager().addMapListener(this);
        tiledMapLayers = new Array<>();

        profiler = new GLProfiler(Gdx.graphics);
//        profiler.enable();
        if (profiler.isEnabled()) {
            box2DDebugRenderer = new Box2DDebugRenderer();
            world = context.getWorld();
        } else {
            box2DDebugRenderer = null;
            world = null;
        }

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(false);

        mapRenderer.setView(gameCamera);

        spriteBatch.begin();
        if (mapRenderer.getMap() != null) {
            AnimatedTiledMapTile.updateAnimationBaseTime();
            for (TiledMapTileLayer layer : tiledMapLayers) {
                mapRenderer.renderTileLayer(layer);
            }
        }

        for (final Entity entity : gameObjectEntities) {
            renderGameObject(entity, delta);
        }

        for (final Entity entity : animatedEntities) {
            renderEntity(entity, delta);
        }
        spriteBatch.end();

        if (profiler.isEnabled()) {
            Gdx.app.debug(TAG, "Bindings: " + profiler.getTextureBindings());
            Gdx.app.debug(TAG, "DrawCalls: " + profiler.getDrawCalls());
            profiler.reset();

            box2DDebugRenderer.render(world, gameCamera.combined);
        }

    }
    private void renderGameObject(Entity entity, float delta) {
        final GameObjectComponent gameObjectComponent = EntityCSEngine.gameObjectCM.get(entity);

        if (gameObjectComponent.animationIndex == -1) {
            return;
        }
        final B2DComponent b2DComponent = EntityCSEngine.b2dCM.get(entity);
        final AnimationComponent animationComponent = EntityCSEngine.animationCM.get(entity);
        final Animation<Sprite> animation = mapAnimations.get(gameObjectComponent.animationIndex);
        final Sprite frame = animation.getKeyFrame(animationComponent.animationTime);


        frame.setBounds(
                b2DComponent.renderPosition.x,
                b2DComponent.renderPosition.y,
                animationComponent.width,
                animationComponent.height);
        frame.setOriginCenter();
        frame.setRotation(b2DComponent.body.getAngle() * MathUtils.radDeg);
        frame.draw(spriteBatch);
    }

    private void renderEntity(Entity entity, float delta) {
        AnimationComponent animationComponent = EntityCSEngine.animationCM.get(entity);

        if (animationComponent.animationType == null) {
            return;
        }

        B2DComponent b2DComponent = EntityCSEngine.b2dCM.get(entity);
        Animation<Sprite> animation = getAnimation(animationComponent.animationType);
        Sprite frame = animation.getKeyFrame(animationComponent.animationTime);


        b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(), delta);

        frame.setBounds(
                    b2DComponent.renderPosition.x - b2DComponent.width * 0.5f,
                    b2DComponent.renderPosition.y - b2DComponent.height * 0.5f,
                    animationComponent.width,
                    animationComponent.height);

        frame.draw(spriteBatch);
    }

    private Animation<Sprite> getAnimation(AnimationType animationType) {
        Animation<Sprite> animation = animationCache.get(animationType);
        if (animation == null) {
            Gdx.app.debug(TAG, "Creating new animation of type " + animationType);
            TextureRegion[][] textureRegions = regionCache.get(animationType.getAtlasKey());
            if (textureRegions == null) {
                Gdx.app.debug(TAG, "Creating texture regions for " + animationType.getAtlasKey());
                TextureAtlas.AtlasRegion atlasRegion = assetManager.get(animationType.getAtlasPath(), TextureAtlas.class).findRegion(animationType.getAtlasKey());
                textureRegions = atlasRegion.split(32, 32);
                regionCache.put(animationType.getAtlasKey(), textureRegions);
            }

            animation = new Animation<>(animationType.getFrameTime(), getKeyFrame(textureRegions[animationType.getRowIndex()]));
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animationCache.put(animationType, animation);
        }

        return animation;
    }

    private Sprite[] getKeyFrame(TextureRegion[] textureRegion) {
        Sprite[] keyFrames = new Sprite[textureRegion.length];

        int i = 0;
        for (TextureRegion region : textureRegion) {
            Sprite sprite = new Sprite(region);
            sprite.setOriginCenter();
            keyFrames[i++] = sprite;
        }
        return keyFrames;
    }


    @Override
    public void dispose() {
        if (box2DDebugRenderer != null) {
            box2DDebugRenderer.dispose();
        }
        mapRenderer.dispose();
    }

    @Override
    public void mapChange(Map map) {
        mapRenderer.setMap(map.getTiledMap());
        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledMapLayers);
        mapAnimations = map.getMapAnimations();

    }
}
