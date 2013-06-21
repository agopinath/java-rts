package org.cyanojay.rts.world.units;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cyanojay.rts.ai.PathFinder;
import org.cyanojay.rts.ai.TerrainNode;
import org.cyanojay.rts.ai.steering.FollowPath;
import org.cyanojay.rts.ai.steering.Pathway;
import org.cyanojay.rts.ai.steering.Separation;
import org.cyanojay.rts.ai.steering.SteeringManager;
import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.map.PathfindingMap;
import org.cyanojay.rts.world.map.Terrain;
import org.cyanojay.rts.world.map.TerrainMap;
import org.cyanojay.rts.world.map.TerrainType;
import org.cyanojay.rts.world.map.Viewport;

import com.agopinath.lthelogutil.Fl;

public class Swarm implements Iterable<Soldier> {
	private Set<Soldier> units;
	private PathfindingMap moveMap;
	private Viewport vp;
	private Pathway path;
	private SteeringManager steer;
	private Soldier leader;
	
	public Swarm(TerrainMap m) {
		units = new HashSet<Soldier>();
		steer = new SteeringManager();
		vp = m.getViewport();
		moveMap = m.getPathMap();
		leader = null;
	}

	@Override
	public Iterator<Soldier> iterator() {
		return units.iterator();
	}

	public void add(Soldier soldier) {
		units.add(soldier);
	}
	
	public void findLeader(int mouseX, int mouseY) { // finds the leader of the swarm based on its proximity to dest
		int[] destRowCol = moveMap.screenToMap(mouseX, mouseY);
		int[] locs = moveMap.mapToScreen(destRowCol[0], destRowCol[1]);
		Vector2f dest = new Vector2f(locs[0], locs[1]);
		float minDist = Float.MAX_VALUE;
		
		for(Soldier s : units) {
			if(Vmath.distBetween(s.getPosition(), dest) < minDist) {
				minDist = Vmath.distBetween(s.getPosition(), dest);
				leader = s;
			}
		}
	}
	
	public void setOverallPath(TerrainNode[] p) { // sets overall path to the destination
		this.path = new Pathway(p);
		steer.addBehavior(new FollowPath(20f, Soldier.MAX_STEER, 1, path), 0.75f);
		steer.addBehavior(new Separation(this), 0.25f);
		
		leader.setCurrPath(path);
		for(Soldier s : units) {
			s.setState(UnitState.MOVING);
		}
	}
	
	public void update() {
		if(path == null) return;
		
		int uniquePaths = 1;
		for(Soldier s : units) {
			if(s.getCurrPath() == null) continue;
			if(s.getState() != UnitState.ARRIVED) {
				updateUnit(s);
			}
			if(!s.getCurrPath().equals(path)) uniquePaths++;
		}
	}
	
	private void updateUnit(Soldier s) {
		Vector2f steerForce = steer.steer(s.getPosition(), s.getVelocity(), s.getCurrPath());
		s.setVelocity(Vmath.setLength(Vmath.add(s.getVelocity(), steerForce), Soldier.MOVE_SPEED));
		s.setPosition(Vmath.add(s.getPosition(), s.getVelocity()));
		
		if(s.atEndOfPath()) {
			if(moveMap.getBlockAt(moveMap.viewportToMap((int)s.getPosition().x, (int)s.getPosition().y)).baseBlock.getType() == TerrainType.DIRT) {
				Vector2f newVel = new Vector2f((float)Math.random(), (float)Math.random());
				newVel = Vmath.setLength(newVel, Soldier.MOVE_SPEED);
				s.setVelocity(newVel);
			} else if(s.equals(leader) || s.getCurrPath().equals(path)) {
				s.setVelocity(Vector2f.ZERO);
				s.setState(UnitState.ARRIVED);
			} else {
				s.setCurrPath(path);
			}
		}
	}

	public void moveToDestination(int[] dest) {
		if(leader == null) return;
		int[] start = moveMap.viewportToMap((int)leader.getPosition().x, (int)leader.getPosition().y);
		GameUtil.changeBright(moveMap.getBlockAt(start), moveMap, 1.4f);
		GameUtil.changeBright(moveMap.getBlockAt(dest), moveMap, 1.4f);
		//Fl.og("" + start[0] + " " + start[1]);
		TerrainNode[] leaderPath = calcPath(start, dest);
		if(leaderPath == null) return;
		for(Soldier s : units) {
			if(s.equals(leader)) continue;
			int[] currStart = moveMap.viewportToMap((int)s.getPosition().x, (int)s.getPosition().y);
			if(currStart[0] == start[0] && currStart[1] == start[1]) {
				s.setCurrPath(path);
				continue;
			}
			float d1 = GameUtil.pathFinderHeuristic(s.getPosition(), leaderPath[leaderPath.length-1].position);
			Vector2f t1 = getNearbyPointOnPath(s, leaderPath);
			float d2 = GameUtil.pathFinderHeuristic(s.getPosition(), t1);
	
			TerrainNode[] soldierPath = null;
			if(d1 < d2) {
				soldierPath = calcPath(currStart, dest);
				s.setCurrPath(new Pathway(soldierPath));
			} else {
				int[] t1loc = moveMap.viewportToMap((int)t1.x, (int)t1.y);
				if(!t1loc.equals(currStart))
					s.setCurrPath(new Pathway(calcPath(currStart, t1loc)));
				else
					s.setCurrPath(path);
			}
		}
		
		setOverallPath(leaderPath);
	}
	
	private Vector2f getNearbyPointOnPath(Soldier s, TerrainNode[] leaderPath) {
		Vector2f nearest = null;
		float minDist = Float.MAX_VALUE;
		for(TerrainNode node : leaderPath) {
			if(GameUtil.pathFinderHeuristic(s.getPosition(), node.position) < minDist) {
				minDist = GameUtil.pathFinderHeuristic(s.getPosition(), node.position);
				nearest = node.position;
			}
		}
	
		return nearest;
	}

	private TerrainNode[] calcPath(int[] startLoc, int[] destLoc) {
		PathFinder finder = new PathFinder();
		ArrayList<TerrainNode> pathList = finder.findPath(moveMap.getBlockAt(startLoc), moveMap.getBlockAt(destLoc), moveMap);
		if(pathList == null) return null;
		for(int i = 0; i < pathList.size(); i++) {
			GameUtil.changeBright(pathList.get(i), moveMap, 1.4f);
		}
		
		return (TerrainNode[]) pathList.toArray();
	}
}
