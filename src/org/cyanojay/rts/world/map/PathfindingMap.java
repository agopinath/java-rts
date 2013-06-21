package org.cyanojay.rts.world.map;

import org.cyanojay.rts.ai.TerrainNode;

import com.agopinath.lthelogutil.Fl;


public class PathfindingMap {
	private final static int SCALE_FACTOR = 4;
	private TerrainNode[][] nodeMap;
	
	Map baseMap;
	
	public PathfindingMap(Map baseMap) {
		this.baseMap = baseMap;
		int baseMapHeight = baseMap.getHeight();
		int baseMapWidth = baseMap.getWidth();
		
		nodeMap = new TerrainNode[baseMapHeight*SCALE_FACTOR][baseMapWidth*SCALE_FACTOR];
		
		for (int row = 0; row < nodeMap.length; row++) {
			System.out.println();
			for (int col = 0; col < nodeMap[0].length; col++) {
				double tmp1 = (double)(row/((double)nodeMap.length/(double)baseMapHeight)); 
				double tmp2 = (double)(col/((double)nodeMap[0].length/(double)baseMapWidth));
				TerrainNode t = new TerrainNode();
				nodeMap[row][col] = new TerrainNode(baseMap.getTerrainAt((int)(tmp1), (int)(tmp2)));
			}
		}
		
		Fl.og("pathmap: " + nodeMap.length + " by " + nodeMap[0].length);
	}
}
