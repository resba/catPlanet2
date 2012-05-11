package com.resba.catplanet.tilegame.sprites;

import com.resba.catplanet.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.30f;

    private boolean onGround;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public void collideHorizontal() {
    	if (getVelocityX() > 0){
            setVelocityX(-(getVelocityX())-(-0.1f));
        }else{
        	setVelocityX(-(getVelocityX())+(-0.1f));
    	}
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
            setVelocityY(-(getVelocityY())-(-0.20f));
        }else{
        	setVelocityY(-(getVelocityY())+(-0.20f));
        }
        
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = true;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = true;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
