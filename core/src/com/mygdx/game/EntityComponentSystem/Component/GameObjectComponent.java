package com.mygdx.game.EntityComponentSystem.Component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Map.GameObjectType;

public class GameObjectComponent implements Component, Pool.Poolable {
    public GameObjectType type;
    public int animationIndex;

    @Override
    public void reset() {
        type = null;
        animationIndex = -1;
    }
}
