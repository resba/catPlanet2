package org.resba.catplanet.tilegame.sprites;

import java.lang.reflect.Constructor;

import org.resba.catplanet.graphics.*;


/**
    An Entity is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.

    <p>Movement and tile collision are driven by
    {@link org.resba.catplanet.tilegame.Physics}; this class only
    tracks state (alive/dying/dead, on the ground or not) and picks
    the animation to show.
*/
public abstract class Entity extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int STATE_RESPAWN = 4;

    private final Animation left;
    private final Animation right;
    private final Animation deadLeft;
    private final Animation deadRight;
    private int state;
    private long stateTime;
    private boolean onGround;
    private boolean wasIdle;

    /**
        Creates a new Entity with the specified Animations.
    */
    public Entity(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor<?> constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    protected Animation getLeftAnimation() {
        return left;
    }

    protected Animation getRightAnimation() {
        return right;
    }

    protected Animation getDeadLeftAnimation() {
        return deadLeft;
    }

    protected Animation getDeadRightAnimation() {
        return deadRight;
    }


    /**
        Gets the maximum speed of this Entity.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the entity when it first appears on screen.
        Normally, the entity starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Entity. The state is one of
        STATE_NORMAL, STATE_DYING, STATE_DEAD or STATE_RESPAWN.
    */
    public int getState() {
        return state;
    }


    /**
        Sets the state of this Entity.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this entity is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this entity is flying (ignores gravity).
    */
    public boolean isFlying() {
        return false;
    }


    /**
        True when the entity is resting on a solid tile. Maintained by
        the Physics step.
    */
    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }


    /**
        Called by Physics when the entity collided with a tile
        horizontally. Default behaviour is to turn around.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called by Physics when the entity collided with a tile
        vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
        Chooses which animation should be displayed this frame.
        Subclasses override this to add extra states (e.g. flying).
    */
    protected Animation selectAnimation() {
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        }
        else if (getVelocityX() > 0) {
            newAnim = right;
        }
        if (state == STATE_DYING) {
            newAnim = (newAnim == left) ? deadLeft : deadRight;
        }
        return newAnim;
    }


    /**
        When true the current animation is held on its first frame
        instead of cycling (e.g. standing still).
    */
    protected boolean isAnimationIdle() {
        return false;
    }


    /**
        Updates the animation and dying timer for this entity.
    */
    public void update(long elapsedTime) {
        Animation newAnim = selectAnimation();
        boolean idle = isAnimationIdle();

        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else if (idle) {
            if (!wasIdle) {
                anim.start();
            }
        }
        else {
            anim.update(elapsedTime);
        }
        wasIdle = idle;

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
