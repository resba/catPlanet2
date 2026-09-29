package org.resba.catplanet.tilegame;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.resba.catplanet.graphics.Sprite;
import org.resba.catplanet.util.CatLabel;



/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

    private Image[][] tiles;
    private List<Sprite> sprites;
    private Sprite player;
    private List<CatLabel> texts;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new ArrayList<Sprite>();
        texts = new ArrayList<CatLabel>();
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return tiles.length;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return tiles[0].length;
    }


    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }


    /**
        Gets the player Sprite.
    */
    public Sprite getPlayer() {
        return player;
    }


    /**
        Sets the player Sprite.
    */
    public void setPlayer(Sprite player) {
        this.player = player;
    }

    public void setPlayerCoordinates(Sprite player, int x, int y){
        player.setX(
                TileMapRenderer.tilesToPixels(x) +
                (TileMapRenderer.tilesToPixels(1) -
                player.getWidth()) / 2);

            // bottom-justify the sprite
            player.setY(
                TileMapRenderer.tilesToPixels(y + 1) -
                player.getHeight());
    }


    /**
        Adds a Sprite object to this map.
    */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }


    /**
        Removes a Sprite object from this map.
    */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }

    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite. Supports remove().
    */
    public Iterator<Sprite> getSprites() {
        return sprites.iterator();
    }

    /**
        Read-only view of the sprites for simple for-each loops.
    */
    public List<Sprite> getSpriteList() {
        return java.util.Collections.unmodifiableList(sprites);
    }

    public void addTexts(CatLabel lbl) {
        texts.add(lbl);
    }

    public void removeTexts(CatLabel lbl) {
        texts.remove(lbl);
    }

    public CatLabel findTexts(int index){
    	return texts.get(index);
    }

    public Iterator<CatLabel> getTexts() {
        return texts.iterator();
    }

}
