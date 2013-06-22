package org.cyanojay.rts.world.map;

import java.awt.Dimension;

public class Viewport {
	private Map wholeMap;
	private Dimension vpArea;
	
	// respectively: top left row, top left column, bottom right row, bottom right column
	public int topLRow, topLCol, botRRow, botRCol; 
	
	public Viewport(Map m, int tlr, int tlc, int brr, int brc, Dimension viewportArea) {
		wholeMap = m;
		topLRow = tlr;
		topLCol = tlc;
		botRRow = brr;
		botRCol = brc;
		
		vpArea = viewportArea;
	}

	public int getTopLRow() {
		return topLRow;
	}

	public void setTopLRow(int topLRow) {
		this.topLRow = topLRow;
	}

	public int getTopLCol() {
		return topLCol;
	}

	public void setTopLCol(int topLCol) {
		this.topLCol = topLCol;
	}

	public int getBotRRow() {
		return botRRow;
	}

	public void setBotRRow(int botRRow) {
		this.botRRow = botRRow;
	}

	public int getBotRCol() {
		return botRCol;
	}
	
	public void setBotRCol(int botRCol) {
		this.botRCol = botRCol;
	}
	
	public void shiftVertically(int shift) {
		if(topLRow + shift < 0 || botRRow + shift >= wholeMap.getHeight()) return;
		
		topLRow += shift;
		botRRow += shift;
	}
	
	public void shiftHorizontally(int shift) {
		if(topLCol + shift < 0 || botRCol + shift >= wholeMap.getWidth()) return;
		
		topLCol += shift;
		botRCol += shift;
	}

	public int getOffsetX() {
		return -topLCol * Terrain.IMG_WIDTH;
	}
	
	public int getOffsetY() {
		return -topLRow * Terrain.IMG_WIDTH;
	}

	public Dimension getViewportArea() {
		return vpArea;
	}
}
