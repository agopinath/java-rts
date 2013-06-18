import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;


public class GameUtil {
	public static BufferedImage deepCopy(BufferedImage img) {
		 ColorModel cm = img.getColorModel();
		 return new BufferedImage(cm, img.copyData(null), cm.isAlphaPremultiplied(), null);
	}
	
	public static boolean pntInTriangle(double px, double py, double x1, double x2, double x3, double y1, double y2,  double y3) {

	    double o1 = getOrientationResult(x1, y1, x2, y2, px, py);
	    double o2 = getOrientationResult(x2, y2, x3, y3, px, py);
	    double o3 = getOrientationResult(x3, y3, x1, y1, px, py);

	    return (o1 == o2) && (o2 == o3);
	}

	private static int getOrientationResult(double x1, double y1, double x2, double y2, double px, double py) {
	    double orientation = ((x2 - x1) * (py - y1)) - ((px - x1) * (y2 - y1));
	    if (orientation > 0) {
	        return 1;
	    }
	    else if (orientation < 0) {
	        return -1;
	    }
	    else {
	        return 0;
	    }
	}
	
	/*public static float pathFinderHeuristic(Terrain start, Terrain dest) {
		float straightDist, dist;
		dist = 0.0f;
		int startX = start.getX();
		int startY = start.getY();
		int destX = dest.getX();
		int destY = dest.getY();
		
		return (float) Math.sqrt((destX - startX) * (destX - startX) + (destY - startY) * (destY - startY));
	}*/
	
	public static float pathFinderHeuristic(TerrainNode start, TerrainNode dest) {
		float dx, dy;//, diagDist, straightDist, horizDist, vertDist, dist;
		final float latCost = 10.0f;
		final float diagCost = 14.1f;
		//float HtoVratio = Terrain.IMG_WIDTH/Terrain.IMG_HEIGHT;
		
		dx = Math.abs(start.x - dest.x);
		dy = Math.abs(start.y - dest.y);
		//diagDist = horizDist = vertDist = straightDist = dist = 0.0f;
		
		//horizDist = dx * HtoVratio;
		//vertDist =  dy * (1/HtoVratio);

		//straightDist = horizDist + vertDist;
		//dist = 1.41f * diagDist + 1.00f * (straightDist - 2.0f*diagDist);
		
		return latCost * (dx + dy) + (diagCost - 2f*latCost) * Math.min(dx, dy);
	}
	
	public static int[] squarify(int row, int mapX, int mapY) {
		int squareX = (row % 2 == 0) ? mapX - (Terrain.IMG_WIDTH/2) : mapX;
		int squareY = mapY + row*(Terrain.IMG_HEIGHT/2);
		
		squareX += Terrain.IMG_WIDTH/2;
		squareY += Terrain.IMG_HEIGHT/2;
		
		return new int[] {squareX, squareY};
	}
	
	public static Terrain[] calcSurroundings(int[] rowAndCol, Map map) {
		Terrain[] surround = new Terrain[9];
		int cenRow = 0;
		int cenCol = 0;
		cenRow = rowAndCol[0];
		cenCol = rowAndCol[1];
		
		/*surround[0][0] = map.getTerrainAt(cenRow-1, cenCol-1);
		surround[0][1] = map.getTerrainAt(cenRow-1, cenCol);
		surround[0][2] = map.getTerrainAt(cenRow-1, cenCol+1);
		surround[1][0] = map.getTerrainAt(cenRow, cenCol-1);
		surround[1][1] = map.getTerrainAt(cenRow, cenCol);
		surround[1][2] = map.getTerrainAt(cenRow, cenCol+1);
		surround[2][0] = map.getTerrainAt(cenRow+1, cenCol-1);
		surround[2][1] = map.getTerrainAt(cenRow+1, cenCol);
		surround[2][2] = map.getTerrainAt(cenRow+1, cenCol+1);*/

		surround[0] = map.getTerrainAt(cenRow, cenCol-1);
		surround[1] = map.getTerrainAt(cenRow-1, cenCol);
		surround[2] = map.getTerrainAt(cenRow-1, cenCol+1);
		surround[3] = map.getTerrainAt(cenRow, cenCol-1);
		surround[4] = map.getTerrainAt(cenRow, cenCol);
		surround[5] = map.getTerrainAt(cenRow, cenCol+1);
		surround[6] = map.getTerrainAt(cenRow+1, cenCol-1);
		surround[7] = map.getTerrainAt(cenRow+1, cenCol);
		surround[8] = map.getTerrainAt(cenRow+1, cenCol+1);
		
		return surround;
	}
	
	public static boolean isBlocked(TerrainNode neighbor) {
		return false;
	}
	
	public static void changeBright(Terrain terr, Map map, float factor) {
		BufferedImage img = (BufferedImage) terr.getImage();
		BufferedImage dest = GameUtil.deepCopy(img);
		RescaleOp rescaleOp = new RescaleOp(factor, 15, null);

		rescaleOp.filter(img, dest);

		terr.setImage(dest);
	}
}
