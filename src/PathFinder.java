import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.swing.JComponent;



public class PathFinder {
	ArrayList<Terrain> pathData;
	PriorityQueue<TerrainNode> openList;
	HashSet<TerrainNode> closedList;
	HashMap<TerrainNode, TerrainNode> cameFrom;
	TerrainNode startNode, goalNode;
	int[] startLoc, destLoc;
	int iterations = 0;
	long startTime;
    JComponent pane;
    
	public PathFinder(JComponent pane) {
        pathData = new ArrayList<Terrain>();
		openList = new PriorityQueue<TerrainNode>();
		closedList = new HashSet<TerrainNode>();
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
		openList = new PriorityQueue<TerrainNode>();
		closedList = new HashSet<TerrainNode>();
		cameFrom = new HashMap<TerrainNode, TerrainNode>();
		startNode = new TerrainNode(start);
		goalNode = new TerrainNode(goal);

		startNode.g_score = 0;
		startNode.h_score = GameUtil.pathFinderHeuristic(startNode, goalNode);
		startNode.f_score = startNode.g_score + startNode.h_score;
		cameFrom.put(startNode, null);
		openList.offer(startNode);
		TerrainNode lastNode = null;
		TerrainNode current = startNode;
		
		GameUtil.changeBright(startNode.baseBlock, map, 5f);
		refresh();

		while(!openList.isEmpty()) {
			lastNode = current;
			current = openList.peek();
			
			if(current.equals(goalNode)) {
                System.out.println("END: " + (System.currentTimeMillis() - startTime));
				cameFrom.put(goalNode, lastNode);
				return reconstructPath(cameFrom, goalNode);
			}
			
			openList.remove(current);		
			closedList.add(current);
			Terrain[] surroundings = GameUtil.calcSurroundings(new int[] {current.baseBlock.getRow(), current.baseBlock.getCol()}, map);
			TerrainNode neighbor = null;
			
			for(Terrain curr : surroundings) {
				neighbor = new TerrainNode(curr);
				if(closedList.contains(neighbor) || GameUtil.isBlocked(neighbor)) continue;
				
				float moveCostToNeighbor = GameUtil.pathFinderHeuristic(current, neighbor);

				float tempGScore = current.g_score + moveCostToNeighbor;
				if(!openList.contains(neighbor) || tempGScore < neighbor.g_score) {	                             
                          neighbor.g_score = tempGScore;
                          neighbor.h_score = GameUtil.pathFinderHeuristic(neighbor, goalNode);
                          neighbor.f_score = neighbor.g_score + neighbor.h_score;
                          openList.offer (neighbor);
                          cameFrom.put(neighbor, current);
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
