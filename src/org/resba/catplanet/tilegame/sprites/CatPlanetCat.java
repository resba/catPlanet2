package org.resba.catplanet.tilegame.sprites;

import org.resba.catplanet.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class CatPlanetCat extends Cat {

    public CatPlanetCat(Animation left, Animation right,
        Animation rotate)
    {
        super(left, right, rotate);
    }


    public float getMaxSpeed() {
        return 0;
    }
    


}
