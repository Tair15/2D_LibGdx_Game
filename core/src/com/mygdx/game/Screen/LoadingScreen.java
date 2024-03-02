package com.mygdx.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.game.Audio.AudioType;
import com.mygdx.game.Game2D;
import com.mygdx.game.Map.MapListener;
import com.mygdx.game.Map.MapType;
import com.mygdx.game.UI.LoadingUI;
import com.mygdx.game.input.GameKeys;
import com.mygdx.game.input.Game2DKeyInputListener;
import com.mygdx.game.input.InputManager;


public class LoadingScreen extends AbstractScreen<LoadingUI> implements Game2DKeyInputListener {

    private  final AssetManager assetManager;
    private boolean isMusicLoaded;


    public LoadingScreen(Game2D context) {
        super(context);
        this.assetManager = context.getAssetManager();

        assetManager.load("character/playerAll.atlas", TextureAtlas.class);
        //load maps
        for(MapType mapType : MapType.values()){
            assetManager.load(mapType.getFilePath(),TiledMap.class);
        }
        //audio
        isMusicLoaded = false;
        for (final AudioType audioType:AudioType.values()){
            if (audioType.isMusic()) {
                assetManager.load(audioType.getFilePath(), Music.class);
            }
            else {
                assetManager.load(audioType.getFilePath(), Sound.class);
            }
        }
    }

    @Override
    protected LoadingUI getScreenUI(final Game2D context) {
        return new LoadingUI(context);
    }


    @Override
    public void render(float delta) {
        assetManager.update();
        if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())){
            isMusicLoaded = true;
            audioManager.playAudio(AudioType.INTRO);
        }
        screenUI.setProgress(assetManager.getProgress());
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
        audioManager.stopCurrentMusic();
    }

    @Override
    public void resize(int width, int height) {

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
        if (assetManager.getProgress() >=1) {
            audioManager.playAudio(AudioType.SELECT);
            context.setScreen(ScreenType.GAME);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys keys) {

    }
}
