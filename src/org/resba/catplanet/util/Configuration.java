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
	
	/**
	 * True when the packaged config says cat-dev=true. In development
	 * mode the HUD shows physics readouts and cats display their ID
	 * instead of their line of text.
	 */
	public boolean isDevelopment(){
		return catDev;
	}
	
	public void load() throws IOException{
		String filename = "config/dev/config.txt";
		ClassLoader classLoader = getClass().getClassLoader();
	    URL url = classLoader.getResource(filename);
	    if (url == null) {
	    	filename = "config/production/config.txt";
	    	url = classLoader.getResource(filename);
	    }
	    if (url == null) {
	    	throw new IOException("No configuration file found");
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
	        
	        line = line.trim();
	        if (!line.startsWith("#") && line.contains("=")) {
	            String key = line.substring(0, line.indexOf('=')).trim();
	            String value = line.substring(line.indexOf('=') + 1).trim();
	            if ("cat-dev".equals(key)) {
	                this.catDev = "true".equalsIgnoreCase(value);
	            }
	        }
	    }
	}
	

	
}
