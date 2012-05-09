package com.resba.catplanet.state;

import java.awt.Graphics2D;

import com.resba.catplanet.input.InputManager;

public interface GameState {


    /**
        Gets the name of this state. Used for
        the checkForStateChange() method.
    */
    public String getName();


    /**
        Returns the name of a state to change to if this state is
        ready to change to another state, or null otherwise.
    */
    public String checkForStateChange();


    /**
        Loads any resources for this state. This method is called
        in a background thread before any GameStates are set.
    */
    public void loadResources(ResourceManager resourceManager);

    /**
        Initializes this state and sets up the input manager
    */
    public void start(InputManager inputManager);


    /**
        Performs any actions needed to stop this state.
    */
    public void stop();


    /**
        Updates world, handles input.
    */
    public void update(long elapsedTime);


    /**
        Draws to the screen.
    */
    public void draw(Graphics2D g);
}