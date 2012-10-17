package org.resba.catplanet.tilegame.sprites;

import java.lang.reflect.Constructor;

import org.resba.catplanet.graphics.Animation;
import org.resba.catplanet.graphics.Sprite;


public class Transition extends Sprite {

	public Transition(Animation anim) {
		super(anim);
	}

    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    
}
