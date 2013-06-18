
public class TerrainNode implements Comparable<TerrainNode> {
	float h_score, g_score, f_score;
	int x, y;
	Terrain baseBlock;
	
	public TerrainNode(Terrain block) {
		this.baseBlock = block;
		x = block.getX() + Terrain.IMG_WIDTH/2;
		y = block.getY() + Terrain.IMG_HEIGHT/2;
	}
	
	public boolean equals(Object other) {
		TerrainNode otherNode = (TerrainNode) other;
		if(otherNode.baseBlock.getX() == this.baseBlock.getX() && 
			otherNode.baseBlock.getY() == this.baseBlock.getY()) return true;
		
		return false;
	}

	public int hashCode() {
		return baseBlock.getType().hashCode();
	}
	
    public int compareTo(TerrainNode otherNode) {
        return (int) (this.f_score - otherNode.f_score);
    }
}
