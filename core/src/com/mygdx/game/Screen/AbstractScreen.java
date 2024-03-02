package com.mygdx.game.Screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.Game2D;
import com.mygdx.game.input.Game2DKeyInputListener;
import com.mygdx.game.input.InputManager;


public abstract class AbstractScreen<T extends Table> implements Screen, Game2DKeyInputListener {
    protected final Game2D context;
    protected final FitViewport viewport;
    protected World world;
    protected final Box2DDebugRenderer box2DDebugRenderer;
    protected final T screenUI;
    protected final Stage stage;
    protected final InputManager inputManager;
    protected final AudioManager audioManager;


    public AbstractScreen(Game2D context) {
        this.context = context;
        viewport = context.getScreenViewport();
        this.world = context.getWorld();
        this.box2DDebugRenderer = context.getBox2DDebugRenderer();
        inputManager = context.getInputManager();

        stage = context.getStage();
        screenUI = getScreenUI(context);
        audioManager = context.getAudioManager();
    }

    protected abstract T getScreenUI(final Game2D context);
    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void show() {
        inputManager.addInputListeners(this);
        stage.addActor(screenUI);
    }

    @Override
    public void hide() {
        inputManager.removeInputListeners(this);
        stage.getRoot().removeActor(screenUI);
    }
}
