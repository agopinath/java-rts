package org.cyanojay.rts.ai;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.swing.JComponent;

import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.world.map.PathfindingMap;
import org.cyanojay.rts.world.map.Terrain;

import com.agopinath.lthelogutil.Fl;



public class PathFinder {
	private ArrayList<TerrainNode> pathData;
	private PriorityQueue<TransientTerrainNode> openSet;
	private HashSet<TransientTerrainNode> closedSet;
	private HashMap<TransientTerrainNode, TransientTerrainNode> cameFrom;
	
	private TerrainNode startNode, goalNode;
    private JComponent pane;
    
	public PathFinder(JComponent pane) {
        pathData = new ArrayList<TerrainNode>();
		openSet = new PriorityQueue<TransientTerrainNode>();
		closedSet = new HashSet<TransientTerrainNode>();
		cameFrom = new HashMap<TransientTerrainNode, TransientTerrainNode>();
		this.pane = pane;
	}
	
	public PathFinder() {
		this(null);
	}
	
	public ArrayList<TerrainNode> findPath(TerrainNode start, TerrainNode goal, PathfindingMap map) {
		/*if(start.equals(goal)) {
			Fl.err("Start == goal");
			return null;
		}*/
		pathData = new ArrayList<TerrainNode>();
		
		closedSet = new HashSet<TransientTerrainNode>();
		openSet = new PriorityQueue<TransientTerrainNode>();
		cameFrom = new HashMap<TransientTerrainNode, TransientTerrainNode>();
		
		TransientTerrainNode startNode = start.toTransient();
		TransientTerrainNode goalNode = goal.toTransient();
		startNode.g_score = 0;
		startNode.f_score = startNode.g_score + GameUtil.pathFinderHeuristic(start, goal);
		openSet.offer(startNode);
		
		TransientTerrainNode lastNode = null;
		TransientTerrainNode current = startNode;
		
		while(!openSet.isEmpty()) {
			lastNode = current;
			current = openSet.peek();
			
			if(current.equals(goalNode)) {
				cameFrom.put(goalNode, lastNode);
				return reconstructPath(cameFrom, goalNode);
			}
			
			openSet.remove(current);		
			closedSet.add(current);
			TerrainNode[] surroundings = map.getSurroundings(current.parent.row, current.parent.col);
			
			int idx = 0;
			float moveCostToNeighbor;
			for(TerrainNode neighbor : surroundings) {
				if(neighbor == null) continue;
				TransientTerrainNode ttnode = neighbor.toTransient();
				if(closedSet.contains(neighbor) || GameUtil.isBlocked(neighbor) || 
					!GameUtil.isValidLocation(map, current.parent.row, current.parent.col, 
					neighbor.row, neighbor.col, false)) continue;
					
				if(idx == 0 || idx == 2 || idx == 6 || idx == 8) { // assign movement cost according to position
					moveCostToNeighbor = 14.14f;				   // of neighbor node relative to current node
				} else {
					moveCostToNeighbor = 10.00f;
				}
				
				float tentativeGScore = current.g_score + moveCostToNeighbor;
				if (!openSet.contains(neighbor)|| tentativeGScore < ttnode.g_score) {
					cameFrom.put(ttnode, current);
					ttnode.g_score = tentativeGScore;
					ttnode.f_score = ttnode.g_score + GameUtil.pathFinderHeuristic(ttnode.parent, goalNode.parent);
					if(!openSet.contains(ttnode))
						openSet.offer(ttnode);
				}
				idx++;
			}
		}
		
		return null;
	}
	
	private void refresh() {
		if(pane != null)
			pane.paintImmediately(0, 0, pane.getWidth(), pane.getHeight());       
	}
	
	private ArrayList<TerrainNode> reconstructPath(HashMap<TransientTerrainNode, TransientTerrainNode> parents, TransientTerrainNode goal) {
		TransientTerrainNode currNode = goal;
		
		while(currNode != null) {
			pathData.add(currNode.parent);
			currNode = parents.get(currNode);
		}
		
		Collections.reverse(pathData); // reverse the list because it is currently ordered from the goal to the start
		
		return pathData;
	}
}
