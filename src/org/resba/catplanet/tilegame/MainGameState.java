package org.resba.catplanet.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.resba.catplanet.graphics.*;
import org.resba.catplanet.input.*;
import org.resba.catplanet.sound.*;
import org.resba.catplanet.state.*;
import org.resba.catplanet.tilegame.sprites.*;
import org.resba.catplanet.util.CatCounter;
import org.resba.catplanet.util.Recorder;
import org.resba.catplanet.util.Configuration;


public class MainGameState implements GameState {

    private static final int DRUM_TRACK = 1;

    /**
        Falling this many pixels below the bottom of the map counts as
        dying (maps are expected to have floors, this is a safety net).
    */
    private static final int OUT_OF_WORLD_MARGIN = 256;

    private SoundManager soundManager;
    private MidiPlayer midiPlayer;
    private CatPlanetResourceManager resourceManager;
    private int width;
    private int height;

    private Sound prizeSound;
    private Sound boopSound;
    private Sequence music;
    private TileMap map;
    private TileMapRenderer renderer;
    private Physics physics;

    private String stateChange;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;

    private Recorder r;
    private Configuration cfg;

    public MainGameState(SoundManager soundManager,
        MidiPlayer midiPlayer, int width, int height)
    {
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;
        this.width = width;
        this.height = height;
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        // NORMAL behaviour so the player can tell held from tapped
        // (edge detection lives in Player.control)
        jump = new GameAction("jump");
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);

        renderer = new TileMapRenderer();
        toggleDrumPlayback();
        r = new Recorder();
        try {
            r.load();
        } catch (IOException e) {
            System.err.println("Could not load cat strings: " + e.getMessage());
        }

        cfg = new Configuration();
        renderer.setDebug(cfg.isDevelopment());
    }

    public String getName() {
        return "Main";
    }


    public String checkForStateChange() {
        return stateChange;
    }

    public void setBackground(String bg){
    	renderer.setBackground(resourceManager.loadImage(bg));
    }

    public void loadResources(ResourceManager resManager) {
        resourceManager = (CatPlanetResourceManager)resManager;

        resourceManager.loadResources();

        renderer.setBackground(
            resourceManager.loadImage("background0.png"));

        // load first map
        setMap(resourceManager.loadFirstMap());

        // load sounds
        prizeSound = resourceManager.loadSound("sounds/prize.wav");
        boopSound = resourceManager.loadSound("sounds/boop2.wav");
        music = resourceManager.loadSequence("sounds/music.midi");
    }

    public void start(InputManager inputManager) {
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_D);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(jump, KeyEvent.VK_W);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);

        soundManager.setPaused(false);
        midiPlayer.setPaused(false);
        //midiPlayer.play(music, true);
        toggleDrumPlayback();
    }

    public void stop() {
        soundManager.setPaused(true);
        midiPlayer.setPaused(true);
    }


    public void draw(Graphics2D g) {
        if (map != null) {
            renderer.draw(g, map, width, height);
        }
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Swaps in a new map (or keeps the old one if loading failed).
    */
    private void setMap(TileMap newMap) {
        if (newMap == null) {
            System.err.println("Map failed to load; keeping current map.");
            return;
        }
        map = newMap;
        if (physics == null) {
            physics = new Physics(map);
        }
        else {
            physics.setMap(map);
        }
    }


    private void reloadMap() {
        renderer.removeAllText();
        setMap(resourceManager.reloadMap());
    }


    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stateChange = GameStateManager.EXIT_GAME;
            return;
        }
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            player.control(moveLeft.isPressed(), moveRight.isPressed(),
                jump.isPressed(), elapsedTime);
        }
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is an Entity that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Entity, return false
        if (s1 instanceof Entity && !((Entity)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Entity && !((Entity)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {
        for (Sprite otherSprite : map.getSpriteList()) {
            if (isCollision(sprite, otherSprite)) {
                return otherSprite;
            }
        }
        return null;
    }


    public void respawnPlayer(Respawn r, Player p){
    	p.setX(r.getX());
    	p.setY(r.getY());
    	p.setState(Entity.STATE_NORMAL);
    	p.setVelocityX(0);
    	p.setVelocityY(0);
    	r.setState(Respawn.STATE_NORMAL);
    }

    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        if (map == null) {
            return;
        }
        Player player = (Player)map.getPlayer();

        // player is dead! start map over
        if (player.getState() == Entity.STATE_DEAD) {
            reloadMap();
            return;
        }

        // get keyboard input
        checkInput(elapsedTime);

        // update player; a map transition may replace the map
        // mid-step, in which case everything else waits a frame
        if (!updatePlayer(player, elapsedTime)) {
            return;
        }
        player.update(elapsedTime);

        // update other sprites
        Iterator<Sprite> i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = i.next();
            if (sprite instanceof Entity) {
                Entity creature = (Entity)sprite;
                if (creature.getState() == Entity.STATE_DEAD) {
                    i.remove();
                    continue;
                }
                physics.step(creature, elapsedTime);
            }
            else if (sprite instanceof Cat) {
                Cat raver = (Cat)sprite;
                if (r.stillRaving(raver.getID())) {
                    raver.setState(Cat.STATE_RAVE);
                    raver.setRave(true);
                    raver.canRave(true);
                }
            }
            else if (sprite instanceof Respawn) {
                Respawn res = (Respawn)sprite;
                if (player.getState() == Entity.STATE_RESPAWN
                    && res.getState() == Respawn.STATE_ACTIVE)
                {
                    respawnPlayer(res, player);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }

        // hit a spike with no checkpoint touched yet: restart the map
        if (player.getState() == Entity.STATE_RESPAWN) {
            reloadMap();
        }
    }


    /**
        Moves the player for one step, checking sprite collisions after
        each axis like the original engine. Returns false if the map
        was swapped during the step.
    */
    private boolean updatePlayer(Player player, long elapsedTime) {
        TileMap current = map;

        physics.applyGravity(player, elapsedTime);

        physics.moveHorizontal(player, elapsedTime);
        checkPlayerCollision(player, false);
        if (map != current) {
            return false;
        }

        float oldY = player.getY();
        physics.moveVertical(player, elapsedTime);
        boolean canKill = (oldY < player.getY());
        checkPlayerCollision(player, canKill);
        if (map != current) {
            return false;
        }

        // fell out of the world
        int mapBottom = TileMapRenderer.tilesToPixels(map.getHeight());
        if (player.getY() > mapBottom + OUT_OF_WORLD_MARGIN) {
            player.setState(Entity.STATE_DEAD);
        }
        return true;
    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Entities will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Spike) {
        	player.setState(Entity.STATE_RESPAWN);
        }
        else if (collisionSprite instanceof Entity) {
            Entity badguy = (Entity)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Entity.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.hop();
            }
            else {
                // player dies!
                player.setState(Entity.STATE_DYING);
        	}
        }
        else if (collisionSprite instanceof CatPlanetCat) {
            Cat cuddlycat = (Cat)collisionSprite;
            if(!cuddlycat.isRave()){
            	cuddlycat.setRave(true);
                cuddlycat.setState(Cat.STATE_RAVE);

                // renders the cat text just above the cat
                int textX = Math.round(cuddlycat.getX());
                int textY = Math.round(cuddlycat.getY()) - 6;
                if(!cfg.isDevelopment()){
                    renderer.addText(r.getStringByID(cuddlycat.getID()), textX, textY);
                    r.setInfiniteRave(cuddlycat.getID());
                }else{
                    renderer.addText(cuddlycat.getID(), textX, textY);
                }
                CatCounter.addCat();
            }
        }
        else if (collisionSprite instanceof Transition) {
        	Transition t = (Transition)collisionSprite;
            TileMap next = resourceManager.selectMap(t.getRegion(), t.getMap());
            if (next != null) {
                setBackground("background"+t.getRegion()+".png");
                renderer.removeAllText();
                setMap(next);
            }
        }
        else if (collisionSprite instanceof Respawn) {
            // only the most recently touched checkpoint is active
            for (Sprite s : map.getSpriteList()) {
                if (s instanceof Respawn) {
                    ((Respawn)s).setState(Respawn.STATE_NORMAL);
                }
            }
        	((Respawn)collisionSprite).setState(Respawn.STATE_ACTIVE);
        }
    }


    /**
        Gives the player the specified power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
        }
    }

}
