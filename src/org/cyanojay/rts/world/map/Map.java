package org.cyanojay.rts.world.map;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.agopinath.lthelogutil.Fl;


public class Map {
	private Terrain[][] mapArray;
	private Viewport vp;
	private List<List<Image>> terrainImgs;
	
	
	public Map(File mapFile, File[] assetsDir) {
		loadMapArray(mapFile);
		loadAssets(assetsDir);
		assignAssets();
	}
	
	private void loadMapArray(File mapFile) {
		try {
			Scanner scan = new Scanner(mapFile);
			if(!scan.hasNextLine()) {
				Fl.err("Map file " + mapFile.getAbsolutePath() + " should have at least one line.");
			}
			int cols = scan.nextLine().replaceAll(" ", "").length(); // remove spaces and count length of map width
			int rows = 1;
			while(scan.hasNextLine()) {
				scan.nextLine();
				rows++;
			}
			
			mapArray = new Terrain[rows][cols]; // initialize the map array
			scan.close();
			
			scan = new Scanner(mapFile); // reset scan to beginning of file
			int row = 0;
			int col = 0;
			while(scan.hasNextInt()) {
				char block = (char)scan.nextInt();
				if(block != ' ') {
					switch(block) { // initialize Terrain elems based on map character values
						case 0:
							mapArray[row][col] = new Terrain(row, col, TerrainType.GRASS, null); 
							break;
						case 1:
							mapArray[row][col] = new Terrain(row, col, TerrainType.DIRT, null); 
							break;
						default:
							Fl.err("...terrain type is messed up?");
					}
					col++;
				}
				if(col == cols) { 
					col = 0;
					row++;
				}
			}
			
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void assignAssets() {
		for (int row = 0; row < mapArray.length; row++) {
			for (int col = 0; col < mapArray[row].length; col++) {
				TerrainType type = mapArray[row][col].getType();
				int randImgIdx = (int) (Math.random() * terrainImgs.get(type.ordinal()).size());
				mapArray[row][col].setBaseImage(terrainImgs.get(type.ordinal()).get(randImgIdx));
			}
		}
		
		Terrain.IMG_HEIGHT = terrainImgs.get(0).get(0).getHeight(null); // choose the first image arbitrarily to set as the 
		Terrain.IMG_WIDTH = terrainImgs.get(0).get(0).getWidth(null);   // Terrain Img_height/width field, because it remains constant
		Terrain.AVOID_RADIUS = 64f;//(float) Math.sqrt(2f)*Terrain.IMG_WIDTH + 32f;
		
		for(int row = 0; row < mapArray.length; row++) {
			for(int col = 0; col < mapArray[row].length; col++) {
				int x = col * Terrain.IMG_WIDTH;
				int y = row * Terrain.IMG_HEIGHT;
				mapArray[row][col].setX(x);
				mapArray[row][col].setY(y);
			}
		}
		
		Fl.og(Terrain.IMG_WIDTH + " " + Terrain.IMG_HEIGHT);
	}
	
	private void loadAssets(File[] assetsDirs) {
		terrainImgs = new ArrayList<List<Image>>(10);
		
		int idx = 0;
		for(File tileDir : assetsDirs) {
			File[] assets = tileDir.listFiles();
			terrainImgs.add(new ArrayList<Image>());
			for(File f : assets) {
				try {
					terrainImgs.get(idx).add(ImageIO.read(f));
				} catch (IOException e) {
					Fl.err("Failed to load map assets at file: " + f.getAbsolutePath());
					e.printStackTrace();
					continue;
				}
				Fl.og("Loaded: " + f.getName());
			}
			
			idx++;
		}
	}
	
	public void draw(Graphics2D g) {
		for(int row = vp.getTopLRow(); row <= vp.getBotRRow(); row++) {
			for(int col = vp.getTopLCol(); col <= vp.getBotRCol(); col++) {
				mapArray[row][col].draw(g, vp.getOffsetX(), vp.getOffsetY());
			}
		}
	}
	
	public Terrain getTerrainAt(int row, int col) {
		if(row < 0 || col < 0 || row > mapArray.length || col > mapArray.length)
			return null;
		
		return mapArray[row][col];
	}
	
	public Terrain getTerrainAt(int[] rowcol) {
		return getTerrainAt(rowcol[0], rowcol[1]);
	}
	
	public void setViewport(Viewport vp) {
		this.vp = vp;
	}
	
	public void resetMap() {
		for (int row = 0; row < mapArray.length; row++) {
			for (int col = 0; col < mapArray[row].length; col++) {
				mapArray[row][col].setBaseImage(mapArray[row][col].getBaseImage());
			}
		}
	}
	
	public int[] screenToMap(int x, int y) {
		int row = (y - ((vp != null) ? vp.getOffsetY() : 0)) / Terrain.IMG_HEIGHT;
		int col = (x - ((vp != null) ? vp.getOffsetX() : 0)) / Terrain.IMG_WIDTH;
		
		return new int[] {row, col};
	}
	
	public int[] viewportToMap(int x, int y) {
		int row = y / Terrain.IMG_HEIGHT;
		int col = x / Terrain.IMG_WIDTH;
		
		return new int[] {row, col};
	}
	
	public int[] mapToScreen(int row, int col) {
		Terrain t = mapArray[row][col];
		return new int[] {t.getX() - ((vp != null) ? vp.getOffsetX() : 0), t.getY() - ((vp != null) ? vp.getOffsetY() : 0)};
	}
	
	public int getHeight() {
		return mapArray.length;
	}
	
	public int getWidth() {
		return mapArray[0].length;
	}
	
	public Viewport getViewport() {
		return vp;
	}
}
