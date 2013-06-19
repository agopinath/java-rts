import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.swing.JComponent;

import com.agopinath.lthelogutil.Fl;



public class PathFinder {
	ArrayList<Terrain> pathData;
	PriorityQueue<TerrainNode> openSet;
	HashSet<TerrainNode> closedSet;
	HashMap<TerrainNode, TerrainNode> cameFrom;
	TerrainNode startNode, goalNode;
	int[] startLoc, destLoc;
	int iterations = 0;
	long startTime;
    JComponent pane;
    
	public PathFinder(JComponent pane) {
        pathData = new ArrayList<Terrain>();
		openSet = new PriorityQueue<TerrainNode>();
		closedSet = new HashSet<TerrainNode>();
		cameFrom = new HashMap<TerrainNode, TerrainNode>();
		this.pane = pane;
	}

	public ArrayList<Terrain> getMovePath(int[] startLoc, int[] destLoc, Map map) {
		startTime = System.currentTimeMillis();
		Terrain start = map.getTerrainAt(startLoc[0],startLoc[1]);
		Terrain dest = map.getTerrainAt(destLoc[0], destLoc[1]);
		this.startLoc = startLoc;
		this.destLoc = destLoc;	
		return findPath(start, dest, map);
	}
	
	public ArrayList<Terrain> findPath(Terrain start, Terrain goal, Map map) {
		pathData = new ArrayList<Terrain>();
		
		closedSet = new HashSet<TerrainNode>();
		openSet = new PriorityQueue<TerrainNode>();
		cameFrom = new HashMap<TerrainNode, TerrainNode>();
		
		startNode = new TerrainNode(start);
		goalNode = new TerrainNode(goal);

		startNode.g_score = 0;
		startNode.f_score = startNode.g_score + GameUtil.pathFinderHeuristic(startNode, goalNode);
		openSet.offer(startNode);
		cameFrom.put(startNode, null);
		TerrainNode lastNode = null;
		TerrainNode current = startNode;
		
		GameUtil.changeBright(start, map, 1.4f);
		
		while(!openSet.isEmpty()) {
			lastNode = current;
			current = openSet.peek();
			
			if(current.equals(goalNode)) {
                System.out.println("END: " + (System.currentTimeMillis() - startTime));
				cameFrom.put(goalNode, lastNode);
				return reconstructPath(cameFrom, goalNode);
			}
			
			openSet.remove(current);		
			closedSet.add(current);
			Terrain[] surroundings = GameUtil.calcSurroundings(new int[] {current.baseBlock.getRow(), current.baseBlock.getCol()}, map);
			TerrainNode neighbor = null;
			
			int idx = 0;
			float moveCostToNeighbor;
			for(Terrain curr : surroundings) {
				if(curr == null) continue;
				
				neighbor = new TerrainNode(curr);
				if(closedSet.contains(neighbor) || GameUtil.isBlocked(neighbor)) continue;
					
				if(idx == 0 || idx == 2 || idx == 6 || idx == 8) {
					moveCostToNeighbor = 14.14f;
				} else {
					moveCostToNeighbor = 10.00f;
				}
				
				float tentativeGScore = current.g_score + moveCostToNeighbor;
				if (!openSet.contains(neighbor)|| tentativeGScore < neighbor.g_score) {
					cameFrom.put(neighbor, current);
					neighbor.g_score = tentativeGScore;
					neighbor.f_score = neighbor.g_score + GameUtil.pathFinderHeuristic(neighbor, goalNode);
					if(!openSet.contains(neighbor))
						openSet.offer(neighbor);
				}
			}
		}
		
		return null;
	}
	
	private void refresh() {
		pane.paintImmediately(0, 0, pane.getWidth(), pane.getHeight());       
	}
	
	private ArrayList<Terrain> reconstructPath(HashMap<TerrainNode, TerrainNode> parents, TerrainNode goal) {
		TerrainNode currNode = goal;
		while(parents.get(currNode) != null) {
			pathData.add(currNode.baseBlock);
			currNode = parents.get(currNode);
		}
		
		return pathData;
	}
}
