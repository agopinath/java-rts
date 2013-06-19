
public class TerrainNode implements Comparable<TerrainNode> {
	float g_score, f_score;
	int x, y;
	Terrain baseBlock;
	
	public TerrainNode(Terrain block) {
		this.baseBlock = block;
		x = block.getX();
		y = block.getY();
	}
	
	public boolean equals(Object other) {
		TerrainNode otherNode = (TerrainNode) other;
		if(otherNode.baseBlock == this.baseBlock) return true;
		//if(otherNode.baseBlock.getRow() == this.baseBlock.getRow() &&
		//	otherNode.baseBlock.getCol() == this.baseBlock.getCol()) return true;
		
		return false;
	}

	public int hashCode() {
		return baseBlock.getType().hashCode();//baseBlock.getRow();
	}
	
    public int compareTo(TerrainNode otherNode) {
        return (int) (this.f_score - otherNode.f_score);
    }
}
