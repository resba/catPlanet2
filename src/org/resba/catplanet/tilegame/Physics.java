package org.resba.catplanet.tilegame;

import java.awt.Point;

import org.resba.catplanet.graphics.Sprite;
import org.resba.catplanet.tilegame.sprites.Entity;


/**
    Tile-based movement and collision for Entities on a TileMap.

    <p>Each step is split into three calls (gravity, horizontal move,
    vertical move) so the game can check sprite collisions in between,
    exactly where the original engine did. Movement is swept: every
    tile between the old and new position is tested, so entities can't
    tunnel through walls even on a long step.

    <p>Ground contact is decided by probing the row of tiles directly
    under the entity after the vertical move. This is stable, unlike
    inferring it from whether a collision happened this frame.
*/
public class Physics {

    /** Downward acceleration in px/ms^2. */
    public static final float GRAVITY = 0.0007f;

    /** Nothing falls faster than this (px/ms). */
    public static final float MAX_FALL_SPEED = 0.50f;

    private static final double TILE_SIZE =
        TileMapRenderer.tilesToPixels(1);

    private TileMap map;
    private final Point pointCache = new Point();


    public Physics(TileMap map) {
        this.map = map;
    }


    public void setMap(TileMap map) {
        this.map = map;
    }


    public TileMap getMap() {
        return map;
    }


    /**
        Adds gravity for one step. Entities resting on the ground are
        held at zero vertical speed rather than accumulating a tiny
        downward velocity every frame.
    */
    public void applyGravity(Entity creature, long elapsedTime) {
        if (creature.isFlying()) {
            return;
        }
        if (creature.isOnGround() && creature.getVelocityY() >= 0) {
            creature.setVelocityY(0);
            return;
        }
        float vy = creature.getVelocityY() + GRAVITY * elapsedTime;
        creature.setVelocityY(Math.min(vy, MAX_FALL_SPEED));
    }


    /**
        Moves the entity horizontally, stopping flush against any tile
        it runs into.
    */
    public void moveHorizontal(Entity creature, long elapsedTime) {
        float dx = creature.getVelocityX();
        if (dx == 0) {
            return;
        }
        float newX = creature.getX() + dx * elapsedTime;
        Point tile = getTileCollision(creature, newX, creature.getY());
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
            else {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
    }


    /**
        Moves the entity vertically, landing flush on floors and
        bonking flush against ceilings, then refreshes its ground state.
    */
    public void moveVertical(Entity creature, long elapsedTime) {
        float dy = creature.getVelocityY();
        if (dy != 0) {
            float newY = creature.getY() + dy * elapsedTime;
            Point tile = getTileCollision(creature, creature.getX(), newY);
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
                else {
                    creature.setY(
                        TileMapRenderer.tilesToPixels(tile.y + 1));
                }
                creature.collideVertical();
            }
        }

        if (creature.isFlying()) {
            creature.setOnGround(false);
        }
        else {
            boolean grounded =
                creature.getVelocityY() >= 0 && isSolidBelow(creature);
            creature.setOnGround(grounded);
            if (grounded) {
                // rest exactly on the tile boundary
                creature.setY(
                    TileMapRenderer.tilesToPixels(rowBelow(creature)) -
                    creature.getHeight());
            }
        }
    }


    /**
        Convenience: a full step with no sprite-collision checks in
        between. Used for non-player entities.
    */
    public void step(Entity creature, long elapsedTime) {
        applyGravity(creature, elapsedTime);
        moveHorizontal(creature, elapsedTime);
        moveVertical(creature, elapsedTime);
    }


    /**
        True if any tile in the row immediately below the sprite's
        bottom edge is solid (or off the side of the map). Only
        meaningful when the sprite's bottom edge sits exactly on a tile
        boundary, which landing guarantees.
    */
    public boolean isSolidBelow(Sprite sprite) {
        int row = rowBelow(sprite);
        int fromX = firstTile(sprite.getX());
        int toX = lastTile(sprite.getX(), sprite.getWidth());
        for (int x = fromX; x <= toX; x++) {
            if (isSolid(x, row)) {
                return true;
            }
        }
        return false;
    }


    /** Index of the tile row containing the first pixel row below the sprite. */
    private static int rowBelow(Sprite sprite) {
        return firstTile(sprite.getY() + sprite.getHeight());
    }


    /** First tile index overlapped by a span starting at {@code from}. */
    private static int firstTile(float from) {
        return (int)Math.floor(from / TILE_SIZE);
    }


    /**
        Last tile index overlapped by a span of {@code size} pixels
        starting at {@code to}. The span covers [to, to + size), so the
        last tile is the one containing the pixel just before to + size.
    */
    private static int lastTile(float to, int size) {
        return (int)Math.ceil((to + size) / TILE_SIZE) - 1;
    }


    /**
        Solid means a tile image is present. The left and right edges of
        the map are treated as walls; the top and bottom are open.
    */
    public boolean isSolid(int tileX, int tileY) {
        return tileX < 0 || tileX >= map.getWidth()
            || map.getTile(tileX, tileY) != null;
    }


    /**
        Gets the tile that a Sprite collides with when moving from its
        current position to (newX, newY). Only the Sprite's X or Y
        should be changed, not both. Returns null if no collision is
        detected. The returned Point is reused between calls.
    */
    public Point getTileCollision(Sprite sprite, float newX, float newY) {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations covered by the swept rectangle
        int fromTileX = firstTile(fromX);
        int fromTileY = firstTile(fromY);
        int toTileX = lastTile(toX, sprite.getWidth());
        int toTileY = lastTile(toY, sprite.getHeight());

        // check each tile for a collision
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                if (isSolid(x, y)) {
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }
        return null;
    }

}
