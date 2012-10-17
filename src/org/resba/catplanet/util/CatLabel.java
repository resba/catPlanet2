package org.resba.catplanet.util;

import java.awt.Label;

public class CatLabel extends Label {

	private int id;
	private String str;
	
	public CatLabel(String str, int id){
		this.id = id;
		this.str = str;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void setText(String str){
		this.str = str;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getText(){
		return this.str;
	}

}
