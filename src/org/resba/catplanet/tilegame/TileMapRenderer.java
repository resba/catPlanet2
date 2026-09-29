package org.resba.catplanet.tilegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.resba.catplanet.graphics.Sprite;
import org.resba.catplanet.tilegame.sprites.Entity;
import org.resba.catplanet.util.CatCounter;



/**
    The TileMapRenderer class draws a TileMap on the screen.
    It draws all tiles, sprites, and an optional background image
    centered around the position of the player.

    <p>The camera follows the player on both axes and is clamped to
    the map, so maps taller than the screen scroll vertically. Maps
    smaller than the screen are anchored to the bottom-left.

    <p>If the background image is larger than the screen it scrolls
    at a fraction of the map speed, creating a parallax effect.

    <p>Also, three static methods are provided to convert pixels
    to tile positions, and vice-versa.

    <p>This TileMapRenderer uses a tile size of 64.
*/
public class TileMapRenderer {

    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;
    private static final int TILE_SIZE = 1 << TILE_SIZE_BITS;

    /** A caption drawn at a fixed map position (pixels). */
    private static final class TextLabel {
        final String text;
        final int x;
        final int y;
        TextLabel(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    private Image background;
    private final List<TextLabel> labels = new ArrayList<TextLabel>();
    private boolean debug;

    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        // arithmetic shift floors correctly for negative pixels
        return pixels >> TILE_SIZE_BITS;
    }


    /**
        Converts a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        return numTiles << TILE_SIZE_BITS;
    }


    /**
        Sets the background to draw.
    */
    public void setBackground(Image background) {
        this.background = background;
    }


    /**
        Enables the on-screen physics readout.
    */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    /**
        Adds a caption at the given map position (pixels, baseline).
    */
    public void addText(String label, int px, int py) {
        labels.add(new TextLabel(label, px, py));
    }

    public void removeAllText() {
        labels.clear();
    }


    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }


    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());
        int mapHeight = tilesToPixels(map.getHeight());

        // camera: centre on the player, clamped to the map edges
        int offsetX;
        if (mapWidth <= screenWidth) {
            offsetX = 0;
        }
        else {
            offsetX = screenWidth / 2
                - Math.round(player.getX() + player.getWidth() / 2f);
            offsetX = clamp(offsetX, screenWidth - mapWidth, 0);
        }

        int offsetY;
        if (mapHeight <= screenHeight) {
            offsetY = screenHeight - mapHeight;
        }
        else {
            offsetY = screenHeight / 2
                - Math.round(player.getY() + player.getHeight() / 2f);
            offsetY = clamp(offsetY, screenHeight - mapHeight, 0);
        }

        // clear
        g.setColor(Color.black);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // draw parallax background image
        if (background != null) {
            int bgWidth = background.getWidth(null);
            int bgHeight = background.getHeight(null);

            // when both the map and the background are wider than the
            // screen, scroll the background so that its left edge is on
            // screen at the map's left edge and its right edge at the
            // map's right edge
            int x = 0;
            if (mapWidth > screenWidth && bgWidth > screenWidth) {
                x = (int)((long)offsetX * (screenWidth - bgWidth)
                    / (screenWidth - mapWidth));
            }
            int y = screenHeight - bgHeight;
            if (mapHeight > screenHeight && bgHeight > screenHeight) {
                y = (int)((long)offsetY * (screenHeight - bgHeight)
                    / (screenHeight - mapHeight));
            }
            g.drawImage(background, x, y, null);
        }

        // draw the visible tiles
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        int firstTileY = pixelsToTiles(-offsetY);
        int lastTileY = firstTileY + pixelsToTiles(screenHeight) + 1;
        firstTileX = Math.max(firstTileX, 0);
        firstTileY = Math.max(firstTileY, 0);
        lastTileX = Math.min(lastTileX, map.getWidth() - 1);
        lastTileY = Math.min(lastTileY, map.getHeight() - 1);
        for (int y = firstTileY; y <= lastTileY; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw sprites
        for (Sprite sprite : map.getSpriteList()) {
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);
            // wake up the creature when it's on screen
            if (sprite instanceof Entity &&
                x >= 0 && x < screenWidth)
            {
                ((Entity)sprite).wakeUp();
            }
        }

        // draw player on top
        g.drawImage(player.getImage(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()) + offsetY,
            null);

        // captions
        g.setColor(Color.white);
        for (TextLabel label : labels) {
            g.drawString(label.text, label.x + offsetX, label.y + offsetY);
        }

        // HUD
        g.setColor(Color.white);
        g.drawString("cats x" + CatCounter.getCATS(), 16, 24);
        if (debug) {
            g.drawString(String.format("vel x: %.3f  vel y: %.3f",
                player.getVelocityX(), player.getVelocityY()), 16, 40);
            g.drawString(String.format("pos: %.1f, %.1f  tile: %d, %d",
                player.getX(), player.getY(),
                pixelsToTiles(player.getX()), pixelsToTiles(player.getY())),
                16, 56);
            if (player instanceof Entity) {
                g.drawString("on ground: " + ((Entity)player).isOnGround(),
                    16, 72);
            }
        }
    }

}
