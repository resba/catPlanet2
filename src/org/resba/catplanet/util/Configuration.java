package org.resba.catplanet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JOptionPane;

public class Configuration {
	
	private boolean catDev;
	
	public Configuration(){
		try {
			this.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error[1]: Application not packaged properly. Configuration files are missing. Click Ok to continue with default production configuration.");
			this.catDev = false;
			e.printStackTrace();
		}
	}
	
	//Developement thing
	public boolean isDevelopment(){
		return false;
	}
	
	public void load() throws IOException{
		String filename = "config/dev/config.txt";
		ClassLoader classLoader = getClass().getClassLoader();
	    URL url = classLoader.getResource(filename);
	    if (url == null) {
	    	filename = "config/production/config.txt";
	    	url = classLoader.getResource(filename);
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
	        
	        if (!line.startsWith("#")) {
	            if(line.split("=")[0] == "cat-dev"){
	            	if(line.split("=")[1] == "true"){
	            	this.catDev = true;
	            }else{
	            	this.catDev = false;
	           	}
	           }
	        }
	    }
	}
	

	
}
