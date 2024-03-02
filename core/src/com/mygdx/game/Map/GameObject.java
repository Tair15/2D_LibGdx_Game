package com.mygdx.game.Map;

import com.badlogic.gdx.math.Vector2;

public class GameObject {
    private final GameObjectType type;
    private final Vector2 position;
    private final float width;
    private final float height;
    private final float rootDegree;
    private final int animationIndex;

    public GameObject(GameObjectType type, Vector2 position, float width, float height, float rootDegree, int animationIndex) {
        this.type = type;
        this.position = position;
        this.width = width;
        this.height = height;
        this.rootDegree = rootDegree;
        this.animationIndex = animationIndex;
    }
    public GameObjectType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRootDegree() {
        return rootDegree;
    }

    public int getAnimationIndex() {
        return animationIndex;
    }
}
