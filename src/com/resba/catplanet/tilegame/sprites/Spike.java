package com.resba.catplanet.tilegame.sprites;

import com.resba.catplanet.graphics.Animation;

public class Spike extends Creature {

	public Spike(Animation left, Animation right, Animation deadLeft,
			Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		// TODO Auto-generated constructor stub
	}
    public boolean isFlying() {
        return isAlive();
    }

}
