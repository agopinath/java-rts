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
	public Terrain[][] mapArray;
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
							mapArray[row][col] = new Terrain(TerrainType.EARTH, null); 
							break;
						default:
							mapArray[row][col] = new Terrain(TerrainType.OTHER, null); 
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
		
		Terrain.IMG_HEIGHT = terrainImgs.get(0).getHeight(null); // choose the first image arbitrarily to set as the Terrain Img_height/width field, because it remains constant
		Terrain.IMG_WIDTH = terrainImgs.get(0).getWidth(null);
		
		System.out.println(Terrain.IMG_WIDTH + " " + Terrain.IMG_HEIGHT);
	}
	
	public void draw(Graphics2D g) {
		for(int row = 0; row < mapArray.length; row++) {
			for(int col = 0; col < mapArray[row].length; col++) {
				//int x = (col * Terrain.IMG_HEIGHT * Terrain.IMG_WIDTH)/32 - (row * Terrain.IMG_HEIGHT * Terrain.IMG_WIDTH)/32;
				//int y =(col * Terrain.IMG_HEIGHT * Terrain.IMG_HEIGHT)/32 + (row * Terrain.IMG_HEIGHT * Terrain.IMG_HEIGHT)/32;
				//int hOff = (mapArray.length-1) * (Terrain.IMG_WIDTH/2);
				int x = (row % 2 == 0) ? col*Terrain.IMG_WIDTH + (Terrain.IMG_WIDTH/2) : col*Terrain.IMG_WIDTH;
				int y = row*Terrain.IMG_HEIGHT - (Terrain.IMG_HEIGHT/2*row);//(row > 0) ? row*Terrain.IMG_HEIGHT - (Terrain.IMG_HEIGHT/2);
				mapArray[row][col].draw(g, x, y);
			}
		}
	}
}
