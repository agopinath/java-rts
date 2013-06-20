package org.cyanojay.rts.world.map;
import java.awt.Graphics2D;
import java.awt.Image;

import org.cyanojay.rts.world.Drawable;


public class Terrain implements Drawable {
	public static int IMG_HEIGHT;
	public static int IMG_WIDTH;
	
	private Image img;
	private Image baseImg;
	private TerrainType type;
	private int x, y;
	private int row;
	private int col;
	
	public Terrain(int row, int col, TerrainType type, Image img) {
		this.row = row;
		this.col = col;
		this.type = type;
		this.img = img;
	}

	public void draw(Graphics2D g, int xOff, int yOff) {
		g.drawImage(img, x+xOff, y+yOff, null);
	}

	public Image getImage() {
		return img;
	}

	public void setImage(Image img) {
		this.img = img;
	}
	
	public Image getBaseImage() {
		return baseImg;
	}

	public void setBaseImage(Image baseImg) {
		this.baseImg = baseImg;
		setImage(baseImg);
	}
	
	public TerrainType getType() {
		return type;
	}

	public void setType(TerrainType type) {
		this.type = type;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public boolean equals(Object other) {
		if(((Terrain)other).row == row &&
				((Terrain)other).col == col) return true;
		
		return false;
	}
}
