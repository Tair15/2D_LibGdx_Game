package com.mygdx.game.Items;

import com.mygdx.game.Game2D;

public class Sword extends Item{
    private double damage;

    public Sword(Game2D context ) {
        super(context);
        damage = 0;
        setItemType(ItemType.SWORD);
    }
}
