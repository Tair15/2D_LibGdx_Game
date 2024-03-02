package com.mygdx.game.Map;

import static com.mygdx.game.Game2D.UNIT_SCALE;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class Map {
    public static final String TAG = Map.class.getSimpleName();
    private final TiledMap tiledMap;
    private final Array<CollisionArea> collisionAreas;

    private final Vector2 startLocation;
    private final Array<GameObject> gameObjects;
    private final IntMap<Animation<Sprite>> mapAnimations;

    public Map(final TiledMap tiledMap){
        this.tiledMap = tiledMap;
        collisionAreas = new Array<>();

        parseCollisionLayer();
        startLocation =  new Vector2();
        parsePlayerStartLocation();

        gameObjects = new Array<>();
        mapAnimations = new IntMap<>();

        parseGameObjectLayer();
    }

    private void parseGameObjectLayer() {
        final MapLayer gameObjectsLayer = tiledMap.getLayers().get("gameObjects");
        if(gameObjectsLayer == null){
            Gdx.app.debug(TAG, "There is no gameObjects Layer");
            return;
        }

        MapObjects object = gameObjectsLayer.getObjects();
        for(MapObject mapObject : object){
            if(!(mapObject instanceof TiledMapTileMapObject)){
                Gdx.app.debug(TAG,"GameObject of type "+ mapObject+" is not supported");
                continue;
            }
            TiledMapTileMapObject tiledMapObject = (TiledMapTileMapObject)  mapObject;
            MapProperties tiledMapObjectProperties = tiledMapObject.getProperties();
            MapProperties tiledProperties = tiledMapObject.getTile().getProperties();
            GameObjectType gameObjectType;

            if(tiledMapObjectProperties.containsKey("type")){
                gameObjectType = GameObjectType.valueOf(tiledMapObjectProperties.get("type",String.class));
            }else if(tiledProperties.containsKey("type")){
                gameObjectType = GameObjectType.valueOf(tiledProperties.get("type",String.class));
            }else {
                Gdx.app.log(TAG,"There is no gameObject defined for tile "+tiledMapObjectProperties.get("id", Integer.class));
                continue;
            }

            int animationIndex = tiledMapObject.getTile().getId();
            if(!createAnimation(animationIndex,tiledMapObject.getTile())){
                Gdx.app.log(TAG,"Could not create animation for tile "+ tiledMapObjectProperties.get("id", Integer.class));
                continue;
            }


            float width = tiledMapObjectProperties.get("width", Float.class)*UNIT_SCALE;
            float height = tiledMapObjectProperties.get("height", Float.class)*UNIT_SCALE;
            
            gameObjects.add(
                    new GameObject(gameObjectType, new Vector2(
                            tiledMapObject.getX()*UNIT_SCALE,
                            tiledMapObject.getY()*UNIT_SCALE),
                            width,
                            height,
                            tiledMapObject.getRotation(),
                            animationIndex));
        }
    }

    private boolean createAnimation(int animationIndex, TiledMapTile tile) {
        Animation<Sprite> animation = mapAnimations.get(animationIndex);

        if(animation == null){
            Gdx.app.debug(TAG,"Creating new map animation for the tile "+ tile.getId());
            if(tile instanceof AnimatedTiledMapTile){
                AnimatedTiledMapTile animatedTiledMapTile = (AnimatedTiledMapTile) tile;
                Sprite[] keyFrames = new Sprite[animatedTiledMapTile.getFrameTiles().length];
                int i =0;
                for(StaticTiledMapTile staticTile : animatedTiledMapTile.getFrameTiles()) {
                    keyFrames[i++] = new Sprite(staticTile.getTextureRegion());
                }
                animation = new Animation<>(animatedTiledMapTile.getAnimationIntervals()[0] * 0.001f, keyFrames);
                animation.setPlayMode(Animation.PlayMode.LOOP);
                mapAnimations.put(animationIndex, animation);
            }else if(tile instanceof StaticTiledMapTile){
                animation = new Animation<>(0,new Sprite(tile.getTextureRegion()));
                mapAnimations.put(animationIndex,animation);
            }
            else {
                Gdx.app.log(TAG,"Tile of type "+ tile + " is not supported for map animations");
                return false;
            }
        }
        return true;
    }

    private void parsePlayerStartLocation() {
        final MapLayer startLocationLayer = tiledMap.getLayers().get("playerStartLocation");

        if (startLocationLayer == null) {
            Gdx.app.debug(TAG, "There is no startLocation layer");
            return;
        }

        final MapObjects object = startLocationLayer.getObjects();

        for (final MapObject mapObj : object) {
            if (mapObj instanceof RectangleMapObject) {
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                startLocation.set(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE);
            } else {
                Gdx.app.debug(TAG, "MapObject of type " + mapObj + " is not supported for the playerStartLocation layer!");
            }
        }
    }

    private  void parseCollisionLayer(){
        final MapLayer collisionLayer = tiledMap.getLayers().get("collision");

        if(collisionLayer==null) {
            Gdx.app.debug(TAG, "There is no collision layer");
            return;
        }

        for(final  MapObject mapObj: collisionLayer.getObjects()) {
            if (mapObj instanceof RectangleMapObject) {
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                final float[] rectVertices = new float[10];

                // left-bot
                rectVertices[0] = 0;
                rectVertices[1] = 0;

                //left-top

                rectVertices[2] = 0;
                rectVertices[3] = rectangle.height;

                //right-top

                rectVertices[4] = rectangle.width;
                rectVertices[5] = rectangle.height;

                //right-bot

                rectVertices[6] = rectangle.width;
                rectVertices[7] = 0;

                //left-bot

                rectVertices[8] = 0;
                rectVertices[9] = 0;

                collisionAreas.add(new CollisionArea(rectangle.x,rectangle.y,rectVertices));


            } else if (mapObj instanceof PolylineMapObject) {
                final PolylineMapObject polylineMapObject = (PolylineMapObject) mapObj;
                final Polyline polyline = polylineMapObject.getPolyline();
                collisionAreas.add(new CollisionArea(polyline.getX(),polyline.getY(),polyline.getVertices()));

            } else {
                Gdx.app.debug(TAG, "MapObject of type " + mapObj + " is not supported for the collision layer!");
            }
        }
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public IntMap<Animation<Sprite>> getMapAnimations() {
        return mapAnimations;
    }

    public Array<CollisionArea> getCollisionAreas(){
        return collisionAreas;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
