import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
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
	private List<Image> terrainImgs;
	private static Polygon topL, topR, botL, botR; // used to convert screen coords into map row/col
	
	public Map(File mapFile, File assetsDir) {
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
							mapArray[row][col] = new Terrain(row, col, TerrainType.EARTH, null); 
							break;
						default:
							mapArray[row][col] = new Terrain(row, col, TerrainType.OTHER, null); 
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
				switch (mapArray[row][col].getType()) { // set the images of mapArray elements
					case EARTH:
						mapArray[row][col].setImage(terrainImgs.get((int) (Math.random() * terrainImgs.size())));
						break;
					default:
						Fl.err("No terrain of that type");
				}
			}
		}
		
		Terrain.IMG_HEIGHT = terrainImgs.get(0).getHeight(null); // choose the first image arbitrarily to set as the 
		Terrain.IMG_WIDTH = terrainImgs.get(0).getWidth(null);   // Terrain Img_height/width field, because it remains constant
		
		initPolygons(); // initialize Polygons with new Terrain img height/width
		
		for(int row = 0; row < mapArray.length; row++) {
			for(int col = 0; col < mapArray[row].length; col++) {
				//int x = (col * Terrain.IMG_HEIGHT * Terrain.IMG_WIDTH)/32 - (row * Terrain.IMG_HEIGHT * Terrain.IMG_WIDTH)/32;
				//int y =(col * Terrain.IMG_HEIGHT * Terrain.IMG_HEIGHT)/32 + (row * Terrain.IMG_HEIGHT * Terrain.IMG_HEIGHT)/32;
				//int hOff = (mapArray.length-1) * (Terrain.IMG_WIDTH/2);
				int x = (row % 2 == 0) ? col*Terrain.IMG_WIDTH + (Terrain.IMG_WIDTH/2) : col*Terrain.IMG_WIDTH;
				int y = row*Terrain.IMG_HEIGHT - (Terrain.IMG_HEIGHT/2*row);
				mapArray[row][col].setX(x);
				mapArray[row][col].setY(y);
			}
		}
		
		Fl.og(Terrain.IMG_WIDTH + " " + Terrain.IMG_HEIGHT);
	}
	
	private void loadAssets(File assetsDir) {
		terrainImgs = new ArrayList<Image>();
		
		File[] assets = assetsDir.listFiles();
		for(File f : assets) {
			try {
				terrainImgs.add(ImageIO.read(f));
			} catch (IOException e) {
				Fl.err("Failed to load map assets at file: " + f.getAbsolutePath());
				e.printStackTrace();
				continue;
			}
			Fl.og("Loaded: " + f.getName());
		}
	}
	
	public void draw(Graphics2D g) {
		for(int row = 0; row < mapArray.length; row++) {
			for(int col = 0; col < mapArray[row].length; col++) {
				mapArray[row][col].draw(g);
			}
		}
	}
	
	public Terrain getTerrainAt(int row, int col) {
		return mapArray[row][col];
	}
	
	public void initPolygons() {
		topL = new Polygon(new int[] { 0, Terrain.IMG_WIDTH / 2, 0 },
				new int[] { 0, 0, Terrain.IMG_HEIGHT / 2 }, 3);
		topR = new Polygon(new int[] { Terrain.IMG_WIDTH / 2, Terrain.IMG_WIDTH, Terrain.IMG_WIDTH }, 
				new int[] { 0, Terrain.IMG_HEIGHT / 2, 0 }, 3);
		botL = new Polygon(new int[] { 0, Terrain.IMG_WIDTH / 2, 0 },
				new int[] { Terrain.IMG_HEIGHT / 2, Terrain.IMG_HEIGHT,Terrain.IMG_HEIGHT }, 3);
		botR = new Polygon(new int[] { Terrain.IMG_WIDTH / 2,Terrain.IMG_WIDTH, Terrain.IMG_WIDTH },
				new int[] { Terrain.IMG_HEIGHT, Terrain.IMG_HEIGHT / 2,Terrain.IMG_HEIGHT }, 3);
	}
	
	public static int[] screenToMap(int mouseX, int mouseY, Map terrainMap) {
		int row = -1;
		int col = -1;

		int regionX = (int) (mouseX / Terrain.IMG_WIDTH);
		int regionY = (int) (mouseY * 2 / Terrain.IMG_HEIGHT);

		int mouseMapX = mouseX % Terrain.IMG_WIDTH;
		int mouseMapY = mouseY % Terrain.IMG_HEIGHT;

		int regionDX = 0;
		int regionDY = 0;

		if (topL.contains(mouseMapX, mouseMapY)) {
			//Fl.og("topL");
			regionDX = -1;
			regionDY = -1;
		} else if (topR.contains(mouseMapX, mouseMapY)) {
			//Fl.og("topR");
			regionDX = 0;
			regionDY = -1;
		} else if (botL.contains(mouseMapX, mouseMapY)) {
			//Fl.og("botL");
			regionDX = -1;
			regionDY = 1;
		} else if (botR.contains(mouseMapX, mouseMapY)) {
			//Fl.og("botR");
			regionDX = 0;
			regionDY = 1;
		}

		col = regionX + regionDX;
		row = regionY + regionDY - 4; // weird hack to properly convert coords to rowcol, need to fix later

		return new int[] {row, col};
	}
	
	public static int[] mapToScreen(int row, int col, Map terrainMap) {
		int x = (row % 2 == 0) ? col*Terrain.IMG_WIDTH + (Terrain.IMG_WIDTH/2) : col*Terrain.IMG_WIDTH;
		int y = row*Terrain.IMG_HEIGHT - (Terrain.IMG_HEIGHT/2*row);
		
		return new int[] {x+Terrain.IMG_WIDTH/2, y+Terrain.IMG_HEIGHT}; // translate x and y to center of tile
	}

	public int getHeight() {
		return mapArray.length;
	}
	
	public int getWidth() {
		return mapArray[0].length;
	}

}
