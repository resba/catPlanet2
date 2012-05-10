package com.resba.catplanet.transitions;

public class Warper {

	private int regionID;
	private int mapID;
	private String id;
	
	public Warper(String id, int regionID, int mapID){
		this.regionID = regionID;
		this.mapID = mapID;
		this.id = id;
	}
	
	public void setWarp(int region, int map){
		this.regionID = region;
		this.mapID = map;
	}
	
	public int getRegion(){
		return this.regionID;
	}
	
	public int getMap(){
		return this.mapID;
	}
	
	public String getID(){
		return this.id;
	}
	
}
