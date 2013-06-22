package org.cyanojay.rts.world.units;

import java.awt.Image;
import java.io.File;

public class Sprite {
	private int currImageIdx;
	private Image[] charImgs;
	
	public Sprite(Image[] imgs) {
		this.charImgs = imgs;
	}
	
	public Sprite(String imgsLoc) {
		this(new File(imgsLoc).listFiles());
	}
	
	public Sprite(File[] imgLocs) {
		
	}
	
	private File getByName(File[] imgLocs, String name) {
		for(File f : imgLocs) {
			if(f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		
		return null;
	}
}
