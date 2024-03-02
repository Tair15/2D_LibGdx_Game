package com.mygdx.game.Items;

import com.mygdx.game.Game2D;

public class Item {
    private boolean isOwned;
    private int numberOfItems;
    private ItemType itemType;

    public Item(Game2D context) {
        this.isOwned = false;
        this.numberOfItems = 0;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
}
