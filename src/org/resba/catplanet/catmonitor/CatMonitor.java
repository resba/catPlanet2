package org.resba.catplanet.catmonitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CatMonitor extends JPanel {
	
	JLabel l = new JLabel();
	
	public CatMonitor(){
		loadDef();
	}
	
	public void loadDef(){
		this.setLayout(new GridLayout(0,1,1,1));
		this.setBackground(Color.black);
		this.l.setText("Hello!");
		this.l.setForeground(Color.WHITE);
		this.l.setFont(new Font("Arial", Font.BOLD, 24));
		this.setSize(new Dimension(800,100));
		this.setLocation(0, 0);
		this.setVisible(true);
	}
	
	public void setMonitorText(String text){
		this.l.setText(text);
	}

}
