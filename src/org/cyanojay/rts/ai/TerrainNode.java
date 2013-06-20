package org.cyanojay.rts.ai;

import org.cyanojay.rts.world.map.Terrain;

public class TerrainNode implements Comparable<TerrainNode> {
	public float g_score, f_score;
	public Terrain baseBlock;
	
	public TerrainNode(Terrain block) {
		this.baseBlock = block;
	}
	
	public boolean equals(Object other) {
		TerrainNode otherNode = (TerrainNode) other;
		if(otherNode.baseBlock.getRow() == this.baseBlock.getRow() &&
			otherNode.baseBlock.getCol() == this.baseBlock.getCol()) return true;
		
		return false;
	}

	public int hashCode() {
		return baseBlock.getType().hashCode();//baseBlock.getRow();
	}
	
    public int compareTo(TerrainNode otherNode) {
        return (int) (this.f_score - otherNode.f_score);
    }
}
