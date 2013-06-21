package org.cyanojay.rts.world.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.cyanojay.rts.ai.TerrainNode;
import org.cyanojay.rts.util.vector.Vector2f;

import com.agopinath.lthelogutil.Fl;


public class PathfindingMap extends GameMap {
	private final static int SCALE_FACTOR = 4;
	private final static int BLOCK_SIZE = Terrain.IMG_WIDTH/(SCALE_FACTOR);
	private final static int NODE_OFFSET = BLOCK_SIZE/2;
	private TerrainNode[][] nodeMap;
	
	private TerrainMap baseMap;
	private Viewport vp;
	
	public PathfindingMap(TerrainMap baseMap) {
		this.baseMap = baseMap;
		this.vp = baseMap.getViewport();
		int baseMapHeight = baseMap.getHeight();
		int baseMapWidth = baseMap.getWidth();
		
		nodeMap = new TerrainNode[baseMapHeight*SCALE_FACTOR][baseMapWidth*SCALE_FACTOR];
		
		for (int row = 0; row < nodeMap.length; row++) {
			System.out.println();
			for (int col = 0; col < nodeMap[0].length; col++) {
				double tmp1 = (double)(row/((double)nodeMap.length/(double)baseMapHeight)); 
				double tmp2 = (double)(col/((double)nodeMap[0].length/(double)baseMapWidth));//+ BLOCK_SIZE/2
				nodeMap[row][col] = new TerrainNode(baseMap.getBlockAt((int)(tmp1), (int)(tmp2)), row, col,
													new Vector2f(col*BLOCK_SIZE , row*BLOCK_SIZE), BLOCK_SIZE);
			}
		}
		
		Fl.og("pathmap: " + nodeMap.length + " by " + nodeMap[0].length);
	}
	
	@Override
	public TerrainNode getBlockAt(int row, int col) {
		if(row < 0 || col < 0 || row > nodeMap.length || col > nodeMap.length)
			return null;
		
		return nodeMap[row][col];
	}
	
	public TerrainNode getBlockAt(int[] rowcol) {
		return getBlockAt(rowcol[0], rowcol[1]);
	}

	@Override
	public int[] screenToMap(int x, int y) {
		int row = (y - ((vp != null) ? vp.getOffsetY() : 0)) / BLOCK_SIZE;
		int col = (x - ((vp != null) ? vp.getOffsetX() : 0)) / BLOCK_SIZE;
		
		return new int[] {row, col};
	}

	@Override
	public int[] mapToScreen(int row, int col) {
		TerrainNode n = nodeMap[row][col];
		return new int[] {(int) (n.position.x - ((vp != null) ? vp.getOffsetX() : 0)), (int) (n.position.y - ((vp != null) ? vp.getOffsetY() : 0))};
	}

	@Override
	public TerrainNode[] getSurroundings(int row, int col) {
		TerrainNode[] surroundings = new TerrainNode[9];
		
		surroundings[0] = getBlockAt(row-1, col-1);
		surroundings[1] = getBlockAt(row-1, col);
		surroundings[2] = getBlockAt(row-1, col+1);
		surroundings[3] = getBlockAt(row, col-1);
		surroundings[4] = getBlockAt(row, col);
		surroundings[5] = getBlockAt(row, col+1);
		surroundings[6] = getBlockAt(row+1, col-1);
		surroundings[7] = getBlockAt(row+1, col);
		surroundings[8] = getBlockAt(row+1, col+1);
		
		return surroundings;
	}

	@Override
	public int[] viewportToMap(int x, int y) {
		int row = y / BLOCK_SIZE;
		int col = x / BLOCK_SIZE;
		
		return new int[] {row, col};
	}

	BasicStroke normal = new BasicStroke(1f); 
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.setStroke(normal);
		for(int i = 0; i < nodeMap.length; i++) {
			for(int j = 0; j < nodeMap[0].length; j++) {
				TerrainNode t = nodeMap[i][j];
				g.drawRect((int)t.position.x, (int) t.position.y, t.size, t.size);
			}
		}
	}
}