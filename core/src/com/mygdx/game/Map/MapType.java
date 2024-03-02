package com.mygdx.game.Map;

public enum MapType {
    MAP_1("map/map01/map4.tmx"),
    MAP_2("map/map02/map2.tmx");
    private final String filePath;
    MapType(final String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
