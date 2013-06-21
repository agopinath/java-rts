package org.cyanojay.rts.ai;

public class TransientTerrainNode implements Comparable<TransientTerrainNode> {
	public float g_score, f_score;
	public TerrainNode parent;
	
	protected TransientTerrainNode(TerrainNode parent) {
		this.parent = parent;
	}
	
	public boolean equals(Object other) {
		return parent.uid == ((TransientTerrainNode) other).parent.uid;
	}

	public int hashCode() {
		return (int) parent.uid;
	}
	
    public int compareTo(TransientTerrainNode otherNode) {
        return (int) (this.f_score - otherNode.f_score);
    }
}
