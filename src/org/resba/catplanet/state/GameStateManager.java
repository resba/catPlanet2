package org.resba.catplanet.state;

import java.awt.Image;
import java.awt.Graphics2D;
import java.util.*;

import org.resba.catplanet.input.InputManager;


public class GameStateManager {

    public static final String EXIT_GAME = "_ExitGame";

    private Map<String, GameState> gameStates;
    private Image defaultImage;
    private volatile GameState currentState;
    private InputManager inputManager;
    private boolean done;

    public GameStateManager(InputManager inputManager,
        Image defaultImage)
    {
        this.inputManager = inputManager;
        this.defaultImage = defaultImage;
        gameStates = new HashMap<String, GameState>();
    }

    public void addState(GameState state) {
        gameStates.put(state.getName(), state);
    }

    public Iterator<GameState> getStates() {
        return gameStates.values().iterator();
    }

    public void loadAllResources(ResourceManager resourceManager) {
        for (GameState gameState : gameStates.values()) {
            gameState.loadResources(resourceManager);
        }
    }


    public boolean isDone() {
        return done;
    }


    /**
        Sets the current state (by name).
    */
    public synchronized void setState(String name) {
        // clean up old state
        if (currentState != null) {
            currentState.stop();
        }
        inputManager.clearAllMaps();

        if (EXIT_GAME.equals(name)) {
            done = true;
        }
        else {
            // set new state
            GameState next = gameStates.get(name);
            if (next != null) {
                next.start(inputManager);
            }
            currentState = next;
        }
    }


    /**
        Updates world, handles input. Does nothing until a state has
        been set (the game loop already paces itself).
    */
    public void update(long elapsedTime) {
        GameState state = currentState;
        if (state == null) {
            return;
        }
        String nextState = state.checkForStateChange();
        if (nextState != null) {
            setState(nextState);
        }
        else {
            state.update(elapsedTime);
        }
    }


    /**
        Draws to the screen.
    */
    public void draw(Graphics2D g) {
        GameState state = currentState;
        if (state != null) {
            state.draw(g);
        }
        else {
            // if no state, draw the default image to the screen
            g.drawImage(defaultImage, 0, 0, null);
        }
    }
}
