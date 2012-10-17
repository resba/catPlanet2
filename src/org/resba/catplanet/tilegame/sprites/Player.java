package org.resba.catplanet.tilegame.sprites;

import org.resba.catplanet.graphics.Animation;

/**
    The Player.
*/
public class Player extends FlyingEntity {

    private static final float JUMP_SPEED = -.30f;

    private boolean onGround;
    
    private boolean isJumping;


    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation flyLeft, Animation flyRight)
    {
        super(left, right, deadLeft, deadRight, flyLeft, flyRight);
        this.isJumping = false;
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
            setVelocityY(-(getVelocityY())-(-0.25f));
        }else{
        	setVelocityY(-(getVelocityY())+(-0.25f));
        }
        
        if(getVelocityY() < 0.01 && getVelocityY() > -0.01){
        	setVelocityY(0.0f);
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
    
    public void setJump(boolean j){
    	isJumping = j;
    }
    
    public boolean getJump(){
    	return this.isJumping;
    }


    public float getMaxSpeed() {
        return 0.40f;
    }

}
