package com.mygdx.game.Screen;

import static com.mygdx.game.Game2D.BIT_GROUND;
import static com.mygdx.game.Game2D.BIT_PLAYER;
import static com.mygdx.game.Game2D.UNIT_SCALE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Game2D;
import com.mygdx.game.Map.CollisionArea;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MapListener;
import com.mygdx.game.Map.MapManager;
import com.mygdx.game.Map.MapType;
import com.mygdx.game.UI.GameUI;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.mygdx.game.UI.LoadingUI;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.InputManager;


public class GameScreen extends AbstractScreen<GameUI> implements MapListener {

    private final MapManager mapManager;



    public GameScreen(Game2D context) {
        super(context);

        mapManager = context.getMapManager();
        mapManager.addMapListener(this);
        mapManager.setMap(MapType.MAP_1);

        context.getEntityCSEngine().createPlayer(mapManager.getCurrentMap().getStartLocation(), 0.75f, 0.75f);

    }

    @Override
    protected GameUI getScreenUI(Game2D context) {
        return new GameUI(stage, context.getSkin());
    }



    @Override
    public void render(float delta){
//        TODO remove mapchange test stuff
        if(Gdx.input.isKeyPressed(Input.Keys.L)){
            mapManager.setMap(MapType.MAP_1);
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
            mapManager.setMap(MapType.MAP_2);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {

    }

    @Override
    public void keyUp(InputManager manager, GameKeys keys) {

    }

    @Override
    public void mapChange(Map map) {

    }
}
