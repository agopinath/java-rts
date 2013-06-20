package org.cyanojay.rts.world.units;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cyanojay.rts.ai.PathFinder;
import org.cyanojay.rts.ai.steering.FollowPath;
import org.cyanojay.rts.ai.steering.Pathway;
import org.cyanojay.rts.ai.steering.Separation;
import org.cyanojay.rts.ai.steering.SteeringManager;
import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.map.Map;
import org.cyanojay.rts.world.map.Terrain;
import org.cyanojay.rts.world.map.Viewport;

import com.agopinath.lthelogutil.Fl;

public class Swarm implements Iterable<Soldier> {
	private Set<Soldier> units;
	private Map map;
	private Viewport vp;
	private Pathway path;
	private SteeringManager steer;
	private Soldier leader;
	
	public Swarm(Map m) {
		units = new HashSet<Soldier>();
		steer = new SteeringManager();
		map = m;
		vp = map.getViewport();
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
		int[] destRowCol = map.screenToMap(mouseX, mouseY);
		int[] locs = map.mapToScreen(destRowCol[0], destRowCol[1]);
		Vector2f dest = new Vector2f(locs[0], locs[1]);
		float minDist = Float.MAX_VALUE;
		
		for(Soldier s : units) {
			if(Vmath.distBetween(s.getPosition(), dest) < minDist) {
				minDist = Vmath.distBetween(s.getPosition(), dest);
				leader = s;
			}
		}
	}
	
	public void setOverallPath(Vector2f[] p) { // sets overall path to the destination
		this.path = new Pathway(p);
		steer.addBehavior(new FollowPath(20f, Soldier.MAX_STEER, 1, path), 0.7f);
		steer.addBehavior(new Separation(this), 0.3f);
		
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
		
		if(s.nearingEndOfPath() && !s.getCurrPath().equals(path)) {
			if(s.equals(leader) || s.getCurrPath().equals(path)) {
				float dist = Vmath.distBetween(s.getPosition(), s.getCurrPath().getPathVectorAt(s.getCurrPath().getPathSize()-1));
				s.setVelocity(Vmath.mult(s.getVelocity(), (dist / Soldier.SLOWING_RAD)));
				s.setState(UnitState.ARRIVED);
				
			} else {
				s.setCurrPath(path);
			}
		} else if(s.atEndOfPath()) {
			if(s.equals(leader) || s.getCurrPath().equals(path)) {
				s.setVelocity(Vector2f.ZERO);
				s.setState(UnitState.ARRIVED);
				/*for(Soldier other : units) {
					if(s.getState() != UnitState.ARRIVED || s.equals(other)) continue;
					
				}
				s.setPosition(new Vector2f())*/
			} else {
				s.setCurrPath(path);
			}
		}
	}

	public void moveToDestination(int[] dest) {
		int[] start = GameUtil.unitToMapLoc(leader, map, vp);
		Vector2f[] leaderPath = calcPath(start, dest);
		for(Soldier s : units) {
			if(s.equals(leader)) continue;
			int[] currStart = GameUtil.unitToMapLoc(s, map, vp);
			
			float d1 = GameUtil.pathFinderHeuristic(s.getPosition(), leaderPath[leaderPath.length-1]);
			Vector2f t1 = getNearbyPointOnPath(s, leaderPath);
			float d2 = GameUtil.pathFinderHeuristic(s.getPosition(), t1);
	
			Vector2f[] soldierPath = null;
			if(d1 < d2) {
				soldierPath = calcPath(currStart, dest);
				s.setCurrPath(new Pathway(soldierPath));
			} else {
				soldierPath = calcPath(currStart, map.screenToMap((int)t1.x+vp.getOffsetX(), (int)t1.y+vp.getOffsetY()));
				s.setCurrPath(new Pathway(soldierPath));
			}
		}
		
		setOverallPath(leaderPath);
	}
	
	private Vector2f getNearbyPointOnPath(Soldier s, Vector2f[] leaderPath) {
		Vector2f nearest = null;
		float minDist = Float.MAX_VALUE;
		for(Vector2f v : leaderPath) {
			if(GameUtil.pathFinderHeuristic(s.getPosition(), v) < minDist) {
				minDist = GameUtil.pathFinderHeuristic(s.getPosition(), v);
				nearest = v;
			}
		}
	
		return nearest;
	}

	private Vector2f[] calcPath(int[] startLoc, int[] destLoc) {
		PathFinder finder = new PathFinder();
		ArrayList<Terrain> pathList = finder.findPath(map.getTerrainAt(startLoc), map.getTerrainAt(destLoc), map);
		Vector2f[] vPath = new Vector2f[pathList.size()];
		for(int i = 0; i < pathList.size(); i++) {
			Terrain t = pathList.get(i);
			
			vPath[i] = new Vector2f(t.getX()+Terrain.IMG_WIDTH/2, t.getY()+Terrain.IMG_HEIGHT/2);
			GameUtil.changeBright(t, map, 1.4f);
		}
		
		return vPath;
	}
}
