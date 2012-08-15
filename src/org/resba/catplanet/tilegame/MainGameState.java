package org.resba.catplanet.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.*;

import org.resba.catplanet.graphics.*;
import org.resba.catplanet.input.*;
import org.resba.catplanet.sound.*;
import org.resba.catplanet.state.*;
import org.resba.catplanet.tilegame.sprites.*;
import org.resba.catplanet.util.CatCounter;
import org.resba.catplanet.util.CatLabel;
import org.resba.catplanet.util.Recorder;




public class MainGameState implements GameState {

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.0008f;


    private SoundManager soundManager;
    private MidiPlayer midiPlayer;
    private CatPlanetResourceManager resourceManager;
    private int width;
    private int height;

    private Point pointCache = new Point();
    private Sound prizeSound;
    private Sound boopSound;
    private Sequence music;
    private TileMap map;
    private TileMapRenderer renderer;

    private String stateChange;
    private Graphics2D g;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private long timer;
    
    private Recorder r;

    private float playerVX;

    public MainGameState(SoundManager soundManager,
        MidiPlayer midiPlayer, int width, int height)
    {
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;
        this.width = width;
        this.height = height;
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);

        renderer = new TileMapRenderer();
        toggleDrumPlayback();
    	try {
        	r = new Recorder();
        	r.load();
		} catch (IOException e) {
		}
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
        map = resourceManager.loadFirstMap();

        // load sounds
        prizeSound = resourceManager.loadSound("sounds/prize.wav");
        boopSound = resourceManager.loadSound("sounds/boop2.wav");
        music = resourceManager.loadSequence("sounds/music.midi");
    }

    public void start(InputManager inputManager) {
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
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
        renderer.draw(g, map, width, height);
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

    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stateChange = GameStateManager.EXIT_GAME;
            return;
        }
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
        	float velocityX = player.getVelocityX();
        	if(jump.isPressed()){
                player.jump(true);
            }
        	if(moveLeft.isPressed() || moveRight.isPressed()){
            if (moveLeft.isPressed()) {
            	
            	velocityX += (-0.039f);
            	
            }
            if (moveRight.isPressed()) {
            
            	velocityX -= (-0.039f);
            	
            }
            if (moveLeft.isPressed() && moveRight.isPressed()){
        			//velocityX = 0;
            }
    		if(velocityX > player.getMaxSpeed()){
    			velocityX = player.getMaxSpeed() + 0.039f;
    		}
    		if(velocityX < -(player.getMaxSpeed())){
    			velocityX = -(player.getMaxSpeed() + 0.039f);
    		}
        }else{
        	if(velocityX < 0.01999999 && velocityX > -0.01999999){
        		//velocityX -= (-0.0001f);
        		velocityX = 0;
        	}
        	if(velocityX > 0){
        		//velocityX += (-0.0001f);
        	}
        	if(velocityX == 0){
        		velocityX = 0;
        	}
            if(velocityX > player.getMaxSpeed()){
               velocityX = player.getMaxSpeed();
            }
            if(velocityX < -(player.getMaxSpeed())){
                velocityX = -(player.getMaxSpeed());
            }
        }

            player.setVelocityX(velocityX);
        }

    }

    private void notePlayerX(float x){
         this.playerVX = x;
    }

    public float getPlayerNotedX(){
         return playerVX;
    }

    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
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

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        Entity player = (Entity)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Entity.STATE_DEAD) {
        	/** TODO **/
            renderer.removeAllText();
            map = resourceManager.reloadMap();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Entity) {
                Entity creature = (Entity)sprite;
                if (creature.getState() == Entity.STATE_DEAD) {
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }else if(sprite instanceof Cat){
                	Cat raver = (Cat)sprite;
                	if(r.stillRaving(raver.getID())){
                		raver.setState(Cat.STATE_RAVE);
                		raver.setRave(true);
                		raver.canRave(true);
                	}
                }
            // normal update
            sprite.update(elapsedTime);
        }
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Entity creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
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
        	player.setState(Entity.STATE_DYING);
        }
        else if (collisionSprite instanceof Entity) {
            Entity badguy = (Entity)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Entity.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
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
                
                //renders the cat text.
                
                renderer.addText(r.getStringByID(cuddlycat.getID()),TileMapRenderer.pixelsToTiles(cuddlycat.getX()),TileMapRenderer.pixelsToTiles(cuddlycat.getY()+10));
                
                //Next line is for debugging, espically when your adding, removing, or repositioning cats.
                //Make sure you know what your changing!
                //Dont forget to comment the line above this out if your doing some debug magicks
                
                //renderer.addText(cuddlycat.getID(),TileMapRenderer.pixelsToTiles(cuddlycat.getX()),TileMapRenderer.pixelsToTiles(cuddlycat.getY()+10));
                
                r.setInfiniteRave(cuddlycat.getID());
                renderer.flip(true);
                CatCounter.addCat();
            }
        }
        else if (collisionSprite instanceof Transition) {
        	Transition t = (Transition)collisionSprite;
        	setBackground("background"+t.getRegion()+".png");
            renderer.removeAllText();
        	map = resourceManager.selectMap(t.getRegion(), t.getMap());
        }
    }


    /**
        Gives the player the speicifed power up and removes it
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
            map = resourceManager.selectMap('0', '2');
        }
    }

}