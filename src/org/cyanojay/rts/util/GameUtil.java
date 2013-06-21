package org.cyanojay.rts.util;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import org.cyanojay.rts.ai.TerrainNode;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.world.map.GameMap;
import org.cyanojay.rts.world.map.PathfindingMap;
import org.cyanojay.rts.world.map.TerrainType;

import com.agopinath.lthelogutil.Fl;


public class GameUtil {
	private final static float LATERAL_COST = 10.00f;
	private final static float DIAGONAL_COST = 14.14f;
	
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
	
	public static float pathFinderHeuristic(TerrainNode start, TerrainNode dest) {
		float dx = Math.abs(start.baseBlock.getX() - dest.baseBlock.getX());
		float dy = Math.abs(start.baseBlock.getY() - dest.baseBlock.getY());
		
		return LATERAL_COST * (dx + dy) + (DIAGONAL_COST - 2f*LATERAL_COST) * Math.min(dx, dy);
	}
	
	public static float pathFinderHeuristic(Vector2f start, Vector2f dest) {
		float dx = Math.abs(start.x - dest.x);
		float dy = Math.abs(start.y - dest.y);
		
		return LATERAL_COST * (dx + dy) + (DIAGONAL_COST - 2f*LATERAL_COST) * Math.min(dx, dy);
	}
	
	public static boolean isBlocked(TerrainNode neighbor) {
		return neighbor.baseBlock.getType() == TerrainType.DIRT;
		//return false;
	}
	
	public static boolean isBlocked(GameMap m, int row, int col) {
		return ((TerrainNode) m.getBlockAt(row, col)).baseBlock.getType() == TerrainType.DIRT;
		//return false;
	}

	public static boolean isValidLocation(GameMap m, int sx, int sy, int x, int y, boolean cutCorners) {
		boolean invalid = false;

		if ((!invalid) && ((sx != x) || (sy != y))) {
			invalid = GameUtil.isBlocked(m, x, y);
		}

		// If tile is still invalid, and we are not cutting corners,
		// and the destination node is diagonal from current node,
		// flag the tile invalid if either of the two tiles crossed
		// on the way to it are blocked.
		if (!invalid && !cutCorners) {
			if (x - sx != 0 && y - sy != 0) {
				invalid = GameUtil.isBlocked(m, x, sy) || GameUtil.isBlocked(m, sx, y);
			}
		}
		return !invalid;
	}
	 
	public static void changeBright(TerrainNode terr, PathfindingMap map, float factor) {
		Fl.og("B " + terr.baseBlock.getRow() + " -- " + terr.baseBlock.getCol());
		Fl.og("[" + terr.row + ", " + terr.col + "]");
		BufferedImage img = (BufferedImage) terr.baseBlock.getImage();
		img = GameUtil.deepCopy(img);
		Graphics2D g2 = img.createGraphics();
		Vector2f pos = terr.position;
		g2.setColor(Color.RED);
		g2.fillRect((int)pos.x-terr.baseBlock.getX(), (int)pos.y-terr.baseBlock.getY(), 8, 8);
		/*
		RescaleOp rescaleOp = new RescaleOp(factor, 15, null);

		rescaleOp.filter(img, dest);*/
		//Fl.og("Changing");
		terr.baseBlock.setImage(img);
	}
	
	private static long curUID = 0;
	public static synchronized long getUID() {
		return curUID++;
	}
}
