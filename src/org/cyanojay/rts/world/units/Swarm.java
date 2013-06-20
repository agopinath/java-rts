package org.cyanojay.rts.world.units;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cyanojay.rts.ai.PathFinder;
import org.cyanojay.rts.ai.steering.Pathway;
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
	
	public Swarm(Map m, Viewport view) {
		units = new HashSet<Soldier>();
		steer = new SteeringManager();
		map = m;
		vp = view;
		leader = null;
	}

	@Override
	public Iterator<Soldier> iterator() {
		return units.iterator();
	}

	public void add(Soldier soldier) {
		units.add(soldier);
	}
	
	public Soldier getLeader(int mX, int mY) {
		Vector2f dest = new Vector2f(mX, mY);
		float minDist = Float.MAX_VALUE;
		
		Soldier leader = null;
		for(Soldier s : units) {
			if(Vmath.distBetween(s.getPosition(), dest) < minDist) {
				minDist = Vmath.distBetween(s.getPosition(), dest);
				leader = s;
			}
		}
		
		return leader;
	}
	
	public void setOverallPath(Vector2f[] p) { // sets overall path to the destination
		this.path = new Pathway(p);
		steer.setPath(path);
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
		
		Fl.og("Unique pahts: " + uniquePaths);
		/*if(numArrived == units.size()) {
			Fl.og("Swarm arrived");
			path = null;
		}*/
	}
	
	private void updateUnit(Soldier s) {
		Vector2f steerForce = steer.steerAlongPath(s.getPosition(), s.getVelocity(), 20f, Soldier.MAX_STEER, 1, s.getCurrPath());
		s.setVelocity(Vmath.setLength(Vmath.add(s.getVelocity(), steerForce), Soldier.MOVE_SPEED));
		s.setPosition(Vmath.add(s.getPosition(), s.getVelocity()));
		
		if(s.atEndOfPath()) {
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

	public void moveToDestination(Viewport vp, Soldier currLeader, int[] dest) {
		leader = currLeader;
		int[] start = map.screenToMap((int)leader.getPosition().x+vp.getOffsetX(), (int)leader.getPosition().y+vp.getOffsetY());
		Vector2f[] leaderPath = calcPath(start, dest);
		for(Soldier s : units) {
			if(s.equals(leader)) continue;
			float d1 = GameUtil.pathFinderHeuristic(s.getPosition(), leaderPath[leaderPath.length-1]);
			Vector2f t1 = getNearbyPointOnPath(s, leaderPath);
			float d2 = GameUtil.pathFinderHeuristic(s.getPosition(), t1);
			
			int[] currStart = map.screenToMap((int)s.getPosition().x+vp.getOffsetX(), (int)s.getPosition().y+vp.getOffsetY());
			Vector2f[] soldierPath = null;
			if(d1 < d2) {
				soldierPath = calcPath(currStart, dest);
				s.setCurrPath(new Pathway(soldierPath));
			} else {
				soldierPath = calcPath(currStart, map.screenToMap((int)t1.x, (int)t1.y));
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

	private Vector2f[] calcPath(int[] start, int[] dest) {
		PathFinder finder = new PathFinder();
		ArrayList<Terrain> pathList = finder.findPath(map.getTerrainAt(start[0], start[1]), map.getTerrainAt(dest[0], dest[1]), map);
		Vector2f[] vPath = new Vector2f[pathList.size()];
		for(int i = 0; i < pathList.size(); i++) {
			Terrain t = pathList.get(i);
			
			vPath[i] = new Vector2f(t.getX()+Terrain.IMG_WIDTH/2, t.getY()+Terrain.IMG_HEIGHT/2);
			GameUtil.changeBright(t, map, 1.4f);
		}
		
		return vPath;
	}
}
