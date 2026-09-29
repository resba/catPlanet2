package org.resba.catplanet.tilegame.sprites;

import org.resba.catplanet.graphics.Animation;

/**
    The Player.

    <p>All tuning lives here. Velocities are in pixels per millisecond
    and accelerations in pixels per millisecond squared, matching the
    rest of the engine (a tile is 64 px).

    <p>The feel is deliberately "icy and floaty": the player accelerates
    slowly, keeps sliding after the key is released, and jumps are
    small hops that can be chained in the air.
*/
public class Player extends FlyingEntity {

    // ---- horizontal ----------------------------------------------------

    /** Top running speed (about 4.7 tiles per second). */
    public static final float MAX_RUN_SPEED = 0.30f;

    /** Ground acceleration while a direction key is held (0 to max in ~300 ms). */
    public static final float GROUND_ACCEL = 0.0010f;

    /**
        Ground deceleration with no input. From full speed the player
        slides for roughly 130 px (2 tiles) before stopping: the "ice".
    */
    public static final float GROUND_FRICTION = 0.00035f;

    /** Air acceleration; a bit lower than on the ground. */
    public static final float AIR_ACCEL = 0.0006f;

    /** Air drag with no input; nearly none so hops carry momentum. */
    public static final float AIR_DRAG = 0.00008f;

    /** Below this speed with no input the player is snapped to a stop. */
    private static final float STOP_SPEED = 0.005f;

    // ---- vertical ------------------------------------------------------

    /** Initial upward speed of a hop (about 1.1 tiles high at full hold). */
    public static final float JUMP_SPEED = -0.32f;

    /** Releasing jump while still rising scales the upward speed by this. */
    public static final float JUMP_CUT_FACTOR = 0.5f;

    /**
        Minimum time between two hops. The player may hop again while
        airborne (the maps rely on this to climb), but not faster than
        this, so mashing the key gives a bobbing flight rather than a
        rocket.
    */
    public static final long HOP_COOLDOWN = 140;

    private boolean facingRight = true;
    private boolean jumpHeld;
    private boolean jumpCut;
    private long timeSinceHop = HOP_COOLDOWN;
    private boolean inputLeft;
    private boolean inputRight;


    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight,
        Animation flyLeft, Animation flyRight)
    {
        super(left, right, deadLeft, deadRight, flyLeft, flyRight);
    }


    /**
        Applies one step of player input. Call once per physics step,
        before the Physics step moves the player.

        @param left      move-left key is down
        @param right     move-right key is down
        @param jump      jump key is down (edge detection is done here)
        @param elapsedTime length of this step in ms
    */
    public void control(boolean left, boolean right, boolean jump,
        long elapsedTime)
    {
        inputLeft = left;
        inputRight = right;

        // ---- horizontal --------------------------------------------
        int dir = (right ? 1 : 0) - (left ? 1 : 0);
        if (dir != 0) {
            facingRight = dir > 0;
        }

        float vx = getVelocityX();
        boolean grounded = isOnGround();
        float accel = grounded ? GROUND_ACCEL : AIR_ACCEL;
        float friction = grounded ? GROUND_FRICTION : AIR_DRAG;

        if (dir != 0) {
            // turning against the slide gets friction as well, so a
            // reversal on ice is sluggish but not hopeless
            boolean reversing = vx != 0 && Math.signum(vx) != dir;
            float a = accel + (reversing ? friction : 0);
            vx += dir * a * elapsedTime;
            vx = clamp(vx, -MAX_RUN_SPEED, MAX_RUN_SPEED);
        }
        else {
            vx = applyFriction(vx, friction * elapsedTime);
        }
        setVelocityX(vx);

        // ---- vertical ----------------------------------------------
        timeSinceHop += elapsedTime;
        boolean jumpPressed = jump && !jumpHeld;
        jumpHeld = jump;

        if (jumpPressed && timeSinceHop >= HOP_COOLDOWN) {
            hop();
        }

        // variable height: letting go early cuts the hop short
        if (!jump && !jumpCut && getVelocityY() < 0) {
            setVelocityY(getVelocityY() * JUMP_CUT_FACTOR);
            jumpCut = true;
        }
    }


    /**
        Starts a hop regardless of ground state. Used by control() and
        by the game when the player bounces off something.
    */
    public void hop() {
        setVelocityY(JUMP_SPEED);
        setOnGround(false);
        timeSinceHop = 0;
        jumpCut = false;
    }


    private static float applyFriction(float v, float amount) {
        if (Math.abs(v) <= amount || Math.abs(v) < STOP_SPEED) {
            return 0;
        }
        return v - Math.signum(v) * amount;
    }


    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }


    public boolean isFacingRight() {
        return facingRight;
    }


    /**
        Walls stop the player instead of bouncing them back.
    */
    public void collideHorizontal() {
        setVelocityX(0);
    }


    /**
        Floors and ceilings stop vertical motion. Ground state itself is
        decided by the Physics ground probe, not here.
    */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
        The player never starts moving on its own.
    */
    public void wakeUp() {
        // do nothing
    }


    public float getMaxSpeed() {
        return MAX_RUN_SPEED;
    }


    /**
        Facing comes from input rather than velocity so that sliding
        backwards on ice while pushing the other way looks right.
    */
    protected Animation selectAnimation() {
        if (getState() == STATE_DYING) {
            return facingRight ? getDeadRightAnimation() : getDeadLeftAnimation();
        }
        if (!isOnGround()) {
            return facingRight ? getFlyRightAnimation() : getFlyLeftAnimation();
        }
        return facingRight ? getRightAnimation() : getLeftAnimation();
    }


    /**
        Hold the first walk frame when standing still on the ground.
    */
    protected boolean isAnimationIdle() {
        return isOnGround() && !inputLeft && !inputRight
            && Math.abs(getVelocityX()) < 0.02f;
    }

}
