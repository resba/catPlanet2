package org.resba.catplanet.tilegame.sprites;

import java.lang.reflect.Constructor;

import org.resba.catplanet.graphics.*;


/**
    An Entity that has an additional pair of animations used while it
    is airborne (flying left / flying right).
*/
public abstract class FlyingEntity extends Entity {

    private final Animation flyLeft;
    private final Animation flyRight;

    public FlyingEntity(Animation left, Animation right,
        Animation deadLeft, Animation deadRight,
        Animation flyLeft, Animation flyRight)
    {
        super(left, right, deadLeft, deadRight);
        this.flyLeft = flyLeft;
        this.flyRight = flyRight;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor<?> constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)getLeftAnimation().clone(),
                (Animation)getRightAnimation().clone(),
                (Animation)getDeadLeftAnimation().clone(),
                (Animation)getDeadRightAnimation().clone(),
                (Animation)flyLeft.clone(),
                (Animation)flyRight.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    protected Animation getFlyLeftAnimation() {
        return flyLeft;
    }

    protected Animation getFlyRightAnimation() {
        return flyRight;
    }


    /**
        Shows the flying animation whenever the entity is airborne.
    */
    protected Animation selectAnimation() {
        if (getState() == STATE_DYING || isOnGround()) {
            return super.selectAnimation();
        }
        return getVelocityX() < 0 ? flyLeft : flyRight;
    }

}
