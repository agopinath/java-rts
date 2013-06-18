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
				switch (mapArray[row][col].getType()) { // set the images of mapArray elements
					case GRASS:
						mapArray[row][col].setImage(terrainImgs.get((int) (Math.random() * terrainImgs.size())));
						break;
					case DIRT:
						mapArray[row][col].setImage(terrainImgs.get((int) (Math.random() * terrainImgs.size())));
						break;
					default:
						Fl.err("No terrain of that type");
				}
			}
		}
		
		Terrain.IMG_HEIGHT = terrainImgs.get(0).getHeight(null); // choose the first image arbitrarily to set as the 
		Terrain.IMG_WIDTH = terrainImgs.get(0).getWidth(null);   // Terrain Img_height/width field, because it remains constant
		
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
	
	public static int[] screenToMap(int mouseX, int mouseY, Map terrainMap) {
		
		return null;
	}
	
	public static int[] mapToScreen(int row, int col, Map terrainMap) {
		return null;
	}

	public int getHeight() {
		return mapArray.length;
	}
	
	public int getWidth() {
		return mapArray[0].length;
	}

}
