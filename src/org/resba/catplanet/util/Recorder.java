package org.resba.catplanet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Recorder {
	
	public ArrayList<String> ids;
	public ArrayList<String> lines;
	
	public Recorder(){
	this.ids = new ArrayList<String>();
	this.lines = new ArrayList<String>();
	}
	
	public void load() throws IOException{
		String filename = "strings/cat.txt";
		ClassLoader classLoader = getClass().getClassLoader();
	    URL url = classLoader.getResource(filename);
	    if (url == null) {
	        throw new IOException("No such thing: " + filename);
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
	            ids.add(line.split("=")[0]);
	            JOptionPane.showMessageDialog(null, line.split("=")[0]+" ----- "+line.split("=")[1]);
	            lines.add(line.split("=")[1]);
	        }
	    }
	}
	
	public String getStringByID(String ID){
		return lines.get(ids.indexOf(ID));
	}
	
}
