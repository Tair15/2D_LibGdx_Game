package com.mygdx.game.UI;

public enum AnimationType {
    HERO_MOVE_UP("character/playerAll.atlas","123", 0.05f,0),
    HERO_MOVE_DOWN("character/playerAll.atlas","playerAll", 0.05f,2),
    HERO_MOVE_LEFT("character/playerAll.atlas","playerAll", 0.07f,0),
    HERO_MOVE_RIGHT("character/playerAll.atlas","playerAll", 0.07f,1);



    private final String atlasPath;
    private final String atlasKey;
    private final float frameTime;
    private final int rowIndex;

    AnimationType(String atlasPath, String atlasKey, float frameTime, int rowIndex) {
        this.atlasPath = atlasPath;
        this.atlasKey = atlasKey;
        this.frameTime = frameTime;
        this.rowIndex = rowIndex;
    }

    public String getAtlasPath() {
        return atlasPath;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public float getFrameTime() {
        return frameTime;
    }

    public int getRowIndex() {
        return rowIndex;
    }
}
