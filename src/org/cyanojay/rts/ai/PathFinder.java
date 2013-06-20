package org.cyanojay.rts.ai;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.swing.JComponent;

import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.world.map.Map;
import org.cyanojay.rts.world.map.Terrain;

import com.agopinath.lthelogutil.Fl;



public class PathFinder {
	private ArrayList<Terrain> pathData;
	private PriorityQueue<TerrainNode> openSet;
	private HashSet<TerrainNode> closedSet;
	private HashMap<TerrainNode, TerrainNode> cameFrom;
	private TerrainNode startNode, goalNode;
    private JComponent pane;
    
	public PathFinder(JComponent pane) {
        pathData = new ArrayList<Terrain>();
		openSet = new PriorityQueue<TerrainNode>();
		closedSet = new HashSet<TerrainNode>();
		cameFrom = new HashMap<TerrainNode, TerrainNode>();
		this.pane = pane;
	}
	
	public PathFinder() {
		this(null);
	}
	
	public ArrayList<Terrain> getMovePath(int[] startLoc, int[] destLoc, Map map) {
		if(startLoc.equals(destLoc)) return null;
		Terrain start = map.getTerrainAt(startLoc[0],startLoc[1]);
		Terrain dest = map.getTerrainAt(destLoc[0], destLoc[1]);
		return findPath(start, dest, map);
	}
	
	public ArrayList<Terrain> findPath(Terrain start, Terrain goal, Map map) {
		if(start.equals(goal)) {
			Fl.err("Start == goal");
			return null;
		}
		pathData = new ArrayList<Terrain>();
		
		closedSet = new HashSet<TerrainNode>();
		openSet = new PriorityQueue<TerrainNode>();
		cameFrom = new HashMap<TerrainNode, TerrainNode>();
		
		startNode = new TerrainNode(start);
		goalNode = new TerrainNode(goal);

		startNode.g_score = 0;
		startNode.f_score = startNode.g_score + GameUtil.pathFinderHeuristic(startNode, goalNode);
		openSet.offer(startNode);
		//cameFrom.put(startNode, null);
		TerrainNode lastNode = null;
		TerrainNode current = startNode;
		
		while(!openSet.isEmpty()) {
			lastNode = current;
			current = openSet.peek();
			
			if(current.equals(goalNode)) {
				cameFrom.put(goalNode, lastNode);
				return reconstructPath(cameFrom, goalNode);
			}
			
			openSet.remove(current);		
			closedSet.add(current);
			Terrain[] surroundings = GameUtil.calcSurroundings(new int[] {current.baseBlock.getRow(), current.baseBlock.getCol()}, map);
			TerrainNode neighbor = null;
			
			int idx = 0;
			float moveCostToNeighbor;
			for(Terrain neighborT : surroundings) {
				if(neighborT == null) continue;
				
				neighbor = new TerrainNode(neighborT);
				if(closedSet.contains(neighbor) || GameUtil.isBlocked(neighbor) || 
					!GameUtil.isValidLocation(map, current.baseBlock.getRow(), current.baseBlock.getCol(), 
					neighborT.getRow(), neighborT.getCol(), false)) continue;
					
				if(idx == 0 || idx == 2 || idx == 6 || idx == 8) { // assign movement cost according to position
					moveCostToNeighbor = 14.14f;				   // of neighbor node relative to current node
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
		if(pane != null)
			pane.paintImmediately(0, 0, pane.getWidth(), pane.getHeight());       
	}
	
	private ArrayList<Terrain> reconstructPath(HashMap<TerrainNode, TerrainNode> parents, TerrainNode goal) {
		TerrainNode currNode = goal;
		TerrainNode lastNode = currNode;
		while(currNode != null) {
			pathData.add(currNode.baseBlock);
			currNode = parents.get(currNode);
			/*if(currNode != null && lastNode.equals(currNode)) {
				Fl.og("Same: " + parents.get(currNode));
				continue;
			}
			lastNode = currNode;*/
		}
		
		Collections.reverse(pathData); // reverse the list because it is currently ordered from the goal to the start
		
		return pathData;
	}
}
