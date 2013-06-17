import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;


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
				System.out.println("Map file " + mapFile.getAbsolutePath() + " should have at least one line.");
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
		for( int row = 0; row < mapArray.length; row++ ) {
			System.out.println();
			for( int col = 0; col < mapArray[row].length; col++ ) {
				switch(mapArray[row][col].getType()) { // set the images of mapArray elements
					case EARTH:
						System.out.print("0 ");
						mapArray[row][col].setImage(terrainImgs.get((int) (Math.random()*terrainImgs.size()))); 
						break;
					default:
						System.out.println("No terrain of that type");
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
				System.out.println("Failed to load map assets at file: " + f.getAbsolutePath());
				e.printStackTrace();
				continue;
			}
			System.out.println("Loaded: " + f.getName());
		}
	}
	
	public void draw(Graphics2D g) {
		
	}
}
