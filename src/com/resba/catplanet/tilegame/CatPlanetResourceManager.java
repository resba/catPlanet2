package com.resba.catplanet.tilegame;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import com.resba.catplanet.graphics.*;
import com.resba.catplanet.sound.MidiPlayer;
import com.resba.catplanet.sound.SoundManager;
import com.resba.catplanet.state.ResourceManager;
import com.resba.catplanet.tilegame.sprites.*;
import com.resba.catplanet.transitions.Warper;
import com.resba.catplanet.util.CatLabel;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class CatPlanetResourceManager extends ResourceManager {

    private ArrayList<Image> tiles;
    private ArrayList<Character> regions;
    private ArrayList<Character> maps;
    private String currentMap;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    private Sprite playerSpawn;
    private Sprite catPlanetCat;
    private Transition transition;
    private Sprite leftWallSpike;
    private Sprite rightWallSpike;
    private Sprite ceilingSpike;
    private Sprite groundSpike;
    public int CATS = 0;
    private boolean mapside;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public CatPlanetResourceManager(GraphicsConfiguration gc,
        SoundManager soundManager, MidiPlayer midiPlayer)
    {
        super(gc, soundManager, midiPlayer);
        this.regions = new ArrayList<Character>();
        this.maps = new ArrayList<Character>();
    }


    public void loadResources() {
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }


    public TileMap loadFirstMap() {
    	
    	this.mapside = false;
    	
        TileMap map = null;
            try {
                map = loadMap(
                    "maps/map01.txt",mapside);
            }
            catch (IOException ex) {
                if (currentMap == "map01") {
                    // no maps to load!
                    return null;
                }
                currentMap = "map01";
                map = null;
        }

        return map;
    }
    public TileMap selectMap(char regID, char mapID) {
        TileMap map = null;
            try {
                map = loadMap(
                    "maps/map" + regID +""+ mapID + ".txt", mapside);
                currentMap = "map"+regID+""+mapID;
            }
            catch (IOException ex) {
                if (currentMap == "map01") {
                    // no maps to load!
                    return null;
                }
                currentMap = "map01";
                map = null;
            }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/" + currentMap + ".txt", mapside);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename, boolean trigger)
        throws IOException
    {
    	int spawnX = 0;
    	int spawnY = 0;
        ArrayList<String> lines = new ArrayList<String>();
        int width = 0;
        int height = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(filename);
        if (url == null) {
            throw new IOException("No such map: " + filename);
        }

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(url.openStream()));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }

        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = lines.get(y);
            int respawn = 0;
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y,'a','a');
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y,'a','a');
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y,'a','a');
                }
                /*
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
                */
                //Left-side map respawns
                else if (ch == '-') {
                	Sprite player = (Sprite)playerSprite.clone();

                    newMap.setPlayerCoordinates(player, x, y);

                    // add it to the map
                    newMap.setPlayer(player);
                }
                //Right-side map respawns
                else if (ch == '_') {
                	Sprite player = (Sprite)playerSprite.clone();

                    newMap.setPlayerCoordinates(player, x, y);

                    // add it to the map
                    newMap.setPlayer(player);
                }
                //else if (ch == 'x') {
                	/**TODO: Respawn**/
                	//addSprite(newMap, playerSpawn, x, y,'r',Integer.toString(respawn).charAt(0)); //I'm a hackish little devil I am.
                	//respawn++;
                //}
                else if (ch == 'c') {
                	/**TODO: Cat Text**/
                	addSprite(newMap, catPlanetCat, x, y,'a','a');
                }
                //Spikes
                //Right Wall-spike
                else if(ch == 'd') {
                	addSprite(newMap, rightWallSpike, x,y,'a','a');
                }
                //Left Wall-spike
                else if(ch == 'a') {
                	addSprite(newMap, leftWallSpike, x,y,'a','a');
                }
                //Ground-spike
                else if(ch == 's') {
                	addSprite(newMap, groundSpike, x,y,'a','a');
                }
                //Ceiling Spike
                else if(ch == 'w') {
                	addSprite(newMap, ceilingSpike, x,y,'a','a');
                }
                // Map 3, region 0
                else if (ch == '3' && line.charAt(x-1) == '0'){                
                	addSprite(newMap, transition, x, y,line.charAt(x-1),ch);
                	addSprite(newMap, transition, x-1,y,line.charAt(x-1),ch);
                }
            }
        }

        return newMap;
    }

    private void addWarp(String id, int x, int y){
    	new Warper(id,x,y);
    }
    
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY, char region, char mapI)
    {
    	Sprite sprite = new Sprite(null);
        if (hostSprite != null) {
            // clone the sprite from the "host"
        	
        	if(hostSprite instanceof Transition){
        		sprite = (Transition)hostSprite.clone();
        		sprite.setMap(mapI);
        		sprite.setRegion(region);
        	}else{
        		sprite = (Sprite)hostSprite.clone();
        	}
            

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());
            
            // add it to the map
            map.addSprite(sprite);
            
        }
    }
    
    private void addTexts(TileMap map,
            String str, int tileX, int tileY, int catID)
        {
            	CatLabel lbl = new CatLabel(str, catID);

                lbl.setLocation(
                    TileMapRenderer.tilesToPixels(tileX) +
                    (TileMapRenderer.tilesToPixels(1) -
                    lbl.getWidth()) / 2,TileMapRenderer.tilesToPixels(tileY + 1) -
                    lbl.getHeight());
                
                lbl.setForeground(Color.WHITE);
                
                lbl.setVisible(false);
                
                // add it to the map
                //map.addTexts(lbl);
                
        }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList<Image>();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource("images/" + name);
            if (url == null) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[5][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("cat_1.png"),
            loadImage("cat_2.png"),
            loadImage("cat_3.png"),
            loadImage("cat_4.png"),
            loadImage("cat_5.png"),
            loadImage("cat_6.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
            loadImage("transition.png"),
            loadImage("spikes_down.png"),
            loadImage("spikes_top.png"),
            loadImage("spikes_left.png"),
            loadImage("spikes_right.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
	        images[4] = new Image[] {
	        		loadImage("planet_1.png"),
	        		loadImage("planet_2.png"),
	        		loadImage("planet_3.png"),
	        		loadImage("planet_4.png"),
	        		loadImage("planet_5.png"),
	        		loadImage("planet_6.png"),
	        		loadImage("planet_7.png"),
	        		loadImage("planet_8.png"),
	        		loadImage("planet_9.png"),
	        		loadImage("planet_10.png"),
	        		loadImage("planet_11.png"),
	        		loadImage("planet_12.png"),
	        		loadImage("planet_13.png"),
	        		loadImage("planet_14.png"),
	        		loadImage("planet_15.png"),
	        		loadImage("planet_16.png"),
	        		loadImage("planet_17.png"),
	        		loadImage("planet_18.png"),
	        		loadImage("planet_19.png"),
	        		loadImage("planet_20.png"),
	        		loadImage("planet_21.png"),
	        		loadImage("planet_22.png"),
	        		loadImage("planet_23.png"),
	        		loadImage("planet_24.png"),
	        		loadImage("planet_25.png"),
	        		loadImage("planet_26.png"),
	        		loadImage("planet_27.png"),
	        		loadImage("planet_28.png"),
	        		loadImage("planet_29.png"),
	        		loadImage("planet_30.png"),
	        		loadImage("planet_31.png"),
	        		loadImage("planet_32.png"),
	        		loadImage("planet_33.png"),
	        		loadImage("planet_34.png"),
	        		loadImage("planet_35.png"),
	        		loadImage("planet_36.png"),
	        		loadImage("planet_37.png"),
	        		loadImage("planet_38.png"),
	        		loadImage("planet_39.png"),
	        		loadImage("planet_40.png"),
	        		loadImage("planet_41.png"),
	        		loadImage("planet_42.png"),
	        };
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
            // fix for when I broke it.
        }

        // create creature animations
        Animation[] playerAnim = new Animation[5];
        Animation[] flyAnim = new Animation[5];
        Animation[] grubAnim = new Animation[5];
        Animation[] catAnim = new Animation[5];
        Animation[] tranAnim = new Animation[5];
        Animation[] spikeleftAnim = new Animation[5];
        Animation[] spikerightAnim = new Animation[5];
        Animation[] spikeupAnim = new Animation[5];
        Animation[] spikedownAnim = new Animation[5];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2], images[i][3], images[i][4], images[i][5]);
            flyAnim[i] = createFlyAnim(
                images[i][6], images[i][7], images[i][8]);
            grubAnim[i] = createGrubAnim(
                images[i][9], images[i][10]);
            
            tranAnim[i] = createTranAnim(images[i][11]);
            spikeleftAnim[i] = createSpikeAnim(images[i][14]);
            spikerightAnim[i] = createSpikeAnim(images[i][15]);
            spikedownAnim[i] = createSpikeAnim(images[i][13]);
            spikeupAnim[i] = createSpikeAnim(images[i][12]);
            
        }
        catAnim[4] = createCatAnim(images[4]);
        
        // create creature sprites
        playerSprite = new Player(playerAnim[1], playerAnim[0],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
        catPlanetCat = new CatPlanetCat(playerAnim[0], playerAnim[1], catAnim[4]);
        transition = new Transition(tranAnim[0]);
        leftWallSpike = new Spike(spikeleftAnim[0],spikeleftAnim[1],spikeleftAnim[2],spikeleftAnim[3]);
        rightWallSpike = new Spike(spikerightAnim[0],spikerightAnim[1],spikerightAnim[2],spikerightAnim[3]);
        ceilingSpike = new Spike(spikeupAnim[0],spikeupAnim[1],spikeupAnim[2],spikeupAnim[3]);
        groundSpike = new Spike(spikedownAnim[0],spikedownAnim[1],spikedownAnim[2],spikedownAnim[3]);
        
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4, Image player5, Image player6)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player4, 150);
        anim.addFrame(player5, 150);
        anim.addFrame(player6, 150);
        return anim;
    }


    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }
    
    private Animation createTranAnim(Image img1){
    	Animation anim = new Animation();
    	anim.addFrame(img1, 250);
    	return anim;
    }
    
    private Animation createCatAnim(Image[] images){
    	Animation anim = new Animation();
    	for(int i = 0; images.length != i; i++){
    		anim.addFrame(images[i],100);
    	}
    	return anim;
    }
    
    private Animation createSpikeAnim(Image img1){
    	Animation anim = new Animation();
    	anim.addFrame(img1, 250);
    	return anim;
    }


    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("heart1.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        anim.addFrame(loadImage("heart3.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("star1.png"), 100);
        anim.addFrame(loadImage("star2.png"), 100);
        anim.addFrame(loadImage("star3.png"), 100);
        anim.addFrame(loadImage("star4.png"), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
