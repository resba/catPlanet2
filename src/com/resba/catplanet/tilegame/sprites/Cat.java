package com.resba.catplanet.tilegame.sprites;

import java.lang.reflect.Constructor;

import com.resba.catplanet.graphics.*;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public abstract class Cat extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int STATE_RAVE = 3;
    private boolean rave;
    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private Animation rotate;
    protected int state;
    private long stateTime;
    private ScreenManager s;
    private boolean collision;

    /**
        Creates a new Creature with the specified Animations.
    */
    public Cat(Animation left, Animation right,
        Animation rotate)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.rotate = rotate;
        state = STATE_NORMAL;
        rave = false;
        collision = false;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)rotate.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }
    
    public boolean isRave(){
       if(this.getState() == STATE_RAVE){
           return true;
       }else{
           return false;
       }
    }


    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_RAVE) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this creature is alive.
    */
    public void setRave(boolean t){
    	if (t){
    		rave = t;
    		this.setState(STATE_RAVE);
    	}
    	if(!t){
    		rave = t;
    		this.setState(STATE_NORMAL);
    	}
    }
    public boolean isAlive() {
    	if(rave){
    		return (state == STATE_RAVE);
    	}else{
    		return (state == STATE_NORMAL);
    	}
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }
    
    
    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
        Updates the animaton for this creature.
    */
    
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        }
        else if (getVelocityX() > 0) {
            newAnim = right;
        }
        if (state == STATE_RAVE && newAnim == left) {
            newAnim = rotate;
        }
        else if (state == STATE_RAVE && newAnim == right) {
            newAnim = rotate;
        }
        
        
        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }
    public void showText(String text){
    }
    public void toggleCollision(boolean b){
        this.collision = b;
    }

}
