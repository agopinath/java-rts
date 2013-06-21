package org.cyanojay.rts.ai;

import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.world.map.Terrain;

public class TerrainNode {
	public Terrain baseBlock;
	public Vector2f position;
	public int row, col;
	public int size;
	long uid;
	
	public TerrainNode(Terrain block, int row, int col, Vector2f position, int size) {
		this.baseBlock = block;
		this.row = row;
		this.col = col;
		this.position = position; 
		this.size = size;
		this.uid = GameUtil.getUID();
	}
    
    public TransientTerrainNode toTransient() {
    	return new TransientTerrainNode(this);
    }

    public String toString() {
    	return "[" + row + ", " + col + "]";
    }
}
