package org.cyanojay.rts.ai;

import org.cyanojay.rts.world.map.Terrain;

public class TerrainNode implements Comparable<TerrainNode> {
	public float g_score, f_score;
	public Terrain baseBlock;
	
	public TerrainNode(Terrain block) {
		this.baseBlock = block;
	}
	
	public TerrainNode() {
		
	}

	public boolean equals(Object other) {
		return baseBlock.equals(((TerrainNode) other).baseBlock);
	}

	public int hashCode() {
		return baseBlock.getType().hashCode();//baseBlock.getRow();
	}
	
    public int compareTo(TerrainNode otherNode) {
        return (int) (this.f_score - otherNode.f_score);
    }
    
    public String toString() {
    	return "[" + baseBlock.getRow() + ", " + baseBlock.getCol() + "]";
    }
}
