package com.resba.catplanet.util;

public class CatCounter {
    private static int CATS;
    
    public CatCounter(){
        CATS = 0;
    }
    
    public static void addCat(){
        CATS++;
    }
    
    public static int getCATS(){
        return CATS;
    }
}
