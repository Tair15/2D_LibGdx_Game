package com.mygdx.game.Map;


import static com.mygdx.game.Game2D.BIT_GROUND;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Game2D;

import java.util.EnumMap;


public class MapManager {
    private static final String TAG = MapManager.class.getSimpleName();

    private final World world;
    private final Array<Body> bodies;

    private final AssetManager assetManager;
    private final EntityCSEngine entityCSEngine;
    private final Array<Entity> gameObjectToRemove;

    private MapType currentMapType;
    private Map currentMap;
    private final EnumMap<MapType,Map> mapCache;
    private final Array<MapListener> listeners;

    public MapManager(final Game2D context) {
        currentMapType = null;
        currentMap = null;
        entityCSEngine = context.getEntityCSEngine();

        world = context.getWorld();
        assetManager = context.getAssetManager();
        gameObjectToRemove = new Array<>();
        bodies = new Array<>();
        mapCache = new EnumMap<>(MapType.class);
        listeners = new Array<>();
    }

    public void addMapListener(final MapListener mapListener) {
        listeners.add(mapListener);
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void setMap(final MapType type) {
        if (currentMapType == type) {
            //already set
            return;
        }
        if(currentMap != null) {
            //cleanup curr map entities
            world.getBodies(bodies);
            destroyCollisionAreas();
            destroyGameObjects();
        }

        Gdx.app.debug(TAG,"CHANGING TO MAP "+type);
        currentMap = mapCache.get(type);
        if(currentMap == null){
            Gdx.app.debug(TAG,"Creating new map of type "+ type);
            final TiledMap tiledMap = assetManager.get(type.getFilePath(),TiledMap.class);
            currentMap = new Map(tiledMap);
            mapCache.put(type,currentMap);
        }

        spawnCollisionAreas();
        spawnGameObjects();

        for(MapListener listener : listeners){
            listener.mapChange(currentMap);
        }
    }

    private void spawnGameObjects() {
        for(GameObject gameObject: currentMap.getGameObjects()){

            entityCSEngine.createGameObject(gameObject);
        }
    }

    private void destroyGameObjects() {
        for (Entity entity : entityCSEngine.getEntities()) {
            if (EntityCSEngine.gameObjectCM.get(entity) != null) {
                gameObjectToRemove.add(entity);
            }
        }
        for (Entity entity : gameObjectToRemove) {
            entityCSEngine.removeEntity(entity);
        }
        gameObjectToRemove.clear();
    }

    private void destroyCollisionAreas(){
        for(Body body: bodies){
            if("GROUND".equals(body.getUserData())){
                world.destroyBody(body);
            }
        }
    }

    private void spawnCollisionAreas() {
        Game2D.resetBodyFixtureDefinition();

        for (final CollisionArea collArea : currentMap.getCollisionAreas()) {
            Game2D.BODY_DEF.position.set(collArea.getX(),collArea.getY());
            Game2D.BODY_DEF.fixedRotation =true;
            final Body body = world.createBody(Game2D.BODY_DEF);
            body.setUserData("GROUND");

            Game2D.FIXTURE_DEF.filter.categoryBits = BIT_GROUND;
            Game2D.FIXTURE_DEF.filter.maskBits = -1;
            final ChainShape chainShape = new ChainShape();
            chainShape.createChain(collArea.getVertices());
            Game2D.FIXTURE_DEF.shape = chainShape;
            body.createFixture(Game2D.FIXTURE_DEF);
            chainShape.dispose();
        }
    }
}
