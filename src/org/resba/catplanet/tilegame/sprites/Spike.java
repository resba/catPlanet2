package org.resba.catplanet.tilegame.sprites;

import org.resba.catplanet.graphics.Animation;

public class Spike extends Entity {

	public Spike(Animation left, Animation right, Animation deadLeft,
			Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		// TODO Auto-generated constructor stub
	}
    public boolean isFlying() {
        return isAlive();
    }

}
