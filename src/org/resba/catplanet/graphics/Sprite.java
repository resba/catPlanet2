package org.resba.catplanet.graphics;

import java.awt.Image;

public class Sprite {

    protected Animation anim;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;
    
    private char region;
    private char mapI;
    
    private String currentMap;
    
    private String ID;

    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
        ID = this.getX()+":"+this.getY()+":"+this.getCurrentMap();
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }

    /**
        Clones this Sprite. Does not clone position or velocity
        info.
    */
    public Object clone() {
        return new Sprite(anim);
    }
    
    public char getRegion(){
    	return this.region;
    }

    /**
     * 
     * Char used to identify the current map number (0-9)
     * 
     * @return map number in char
     */
    public char getMap(){
    	return this.mapI;
    }
    
    /**
     * 
     * Char used to identify the current region number (0-9)
     * 
     * @param region number in char
     */
    public void setRegion(char r){
    	this.region = r;
    }

    public void setMap(char m){
    	this.mapI = m;
    }
    
    /**
     * Gets the current map being played in text form.
     * Used to associate IDs with Cats.
     */
    public String getCurrentMap(){
    	return this.currentMap;
    }
    
    public void setCurrentMap(String s){
    	this.currentMap = s;
    }
    
    /**
     * ID is set automatically upon creation of the entity.
     * @return ID of entity
     */
    public String getID(){
    	return this.ID;
    }
    
    public void updateID(){
    	this.ID = this.getX()+":"+this.getY()+":"+this.getCurrentMap();
    }
}
