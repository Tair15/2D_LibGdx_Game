package com.mygdx.game.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {
    private final GameKeys[] keyMapping;
    private final boolean[] keyState;
    private final Array<Game2DKeyInputListener> listeners;

    public InputManager() {
        this.keyMapping = new GameKeys[256];

        for (final GameKeys gameKey : GameKeys.values()) {
            for (final int code : gameKey.keyCode) {
                keyMapping[code] = gameKey;
            }
        }
        keyState = new boolean[GameKeys.values().length];
        listeners = new Array<>();
    }

    public void addInputListeners(final Game2DKeyInputListener listener){
        listeners.add(listener);
    }

    public void removeInputListeners(final Game2DKeyInputListener listener){
        listeners.removeValue(listener, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        final GameKeys gameKeys = keyMapping[keycode];

        if (gameKeys == null) {
            // no mapping
            return false;
        }

        notifyKeyDown(gameKeys);
        return false;
    }

    public void notifyKeyDown(GameKeys gameKey) {
        keyState[gameKey.ordinal()] = true;
        for(final Game2DKeyInputListener listener: listeners){
            listener.keyPressed(this,gameKey);
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        final GameKeys gameKeys = keyMapping[keycode];

        if (gameKeys == null) {
            // no mapping
            return false;
        }

        notifyKeyUp(gameKeys);
        return false;
    }
    public void notifyKeyUp(GameKeys gameKey) {
        keyState[gameKey.ordinal()] = false;
        for (final Game2DKeyInputListener listener : listeners) {
            listener.keyUp(this, gameKey);
        }
    }

    public boolean isKeyPressed(GameKeys gameKey){
        return keyState[gameKey.ordinal()];
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
