package org.cyanojay.rts.world.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cyanojay.rts.ai.PathFinder;
import org.cyanojay.rts.ai.steering.FollowPath;
import org.cyanojay.rts.ai.steering.ObstacleAvoidance;
import org.cyanojay.rts.ai.steering.Pathway;
import org.cyanojay.rts.ai.steering.Separation;
import org.cyanojay.rts.ai.steering.SteeringManager;
import org.cyanojay.rts.ai.steering.SteeringType;
import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.map.Map;
import org.cyanojay.rts.world.map.Terrain;
import org.cyanojay.rts.world.map.TerrainType;
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
		steer.addUnit(soldier);
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
		steer.addBehavior(new FollowPath(20f, Soldier.MAX_STEER, 1, path), 1.5f);
		steer.addBehavior(new Separation(this), 0.2f);
		
		List<Terrain> toAvoid = new ArrayList<Terrain>();
		for(int row = 0; row < map.getHeight(); row++) {
			for(int col = 0; col < map.getWidth(); col++) {
				if(GameUtil.isBlocked(map, row, col)) {
					toAvoid.add(map.getTerrainAt(row, col));
					//Fl.og("[" + row + ", " + col +"]");
				}
			}
		}
		
		steer.addBehavior(new ObstacleAvoidance(toAvoid, Soldier.MAX_STEER), 0.05f);
		
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
		if(!s.isNearPathEnd() && s.nearingEndOfPath()) {
			//steer.removeBehavior(s, 0);
			steer.setWeight(s, 1, 0.5f);			
			s.setNearPathEnd(true);
		}
		
		Vector2f steerForce = steer.steer(s, s.getPosition(), s.getVelocity(), s.getCurrPath());
		s.setVelocity(Vmath.setLength(Vmath.add(s.getVelocity(), steerForce), Soldier.MOVE_SPEED));
		s.setPosition(Vmath.add(s.getPosition(), s.getVelocity()));
		
		 if(s.atEndOfPath()) {
			if(map.getTerrainAt(map.viewportToMap((int)s.getPosition().x, (int)s.getPosition().y)).getType() == TerrainType.DIRT) {
				Vector2f newVel = new Vector2f((float)Math.random(), (float)Math.random());
				newVel = Vmath.setLength(newVel, Soldier.MOVE_SPEED);
				s.setVelocity(newVel);
			} else if(s.equals(leader) || s.getCurrPath().equals(path)) {
				s.setVelocity(Vector2f.ZERO);
				s.setState(UnitState.ARRIVED);
			} else {
				s.setCurrPath(path);
			}
			
			s.setNearPathEnd(false);
		}
	}

	public void moveToDestination(int[] dest) {
		if(leader == null) return;
		int[] start = map.viewportToMap((int)leader.getPosition().x, (int)leader.getPosition().y);
		Vector2f[] leaderPath = calcPath(start, dest);
		if(leaderPath == null) return;
		for(Soldier s : units) {
			if(s.equals(leader)) continue;
			int[] currStart = map.viewportToMap((int)s.getPosition().x, (int)s.getPosition().y);
			if(currStart[0] == start[0] && currStart[1] == start[1]) {
				s.setCurrPath(path);
				continue;
			}
			float d1 = GameUtil.pathFinderHeuristic(s.getPosition(), leaderPath[leaderPath.length-1]);
			Vector2f t1 = getNearbyPointOnPath(s, leaderPath);
			float d2 = GameUtil.pathFinderHeuristic(s.getPosition(), t1);
	
			Vector2f[] soldierPath = null;
			if(d1 < d2) {
				soldierPath = calcPath(currStart, dest);
				s.setCurrPath(new Pathway(soldierPath));
			} else {
				int[] t1loc = map.viewportToMap((int)t1.x, (int)t1.y);
				if(!(Arrays.equals(t1loc, currStart)))
					s.setCurrPath(new Pathway(calcPath(currStart, t1loc)));
				else
					s.setCurrPath(path);
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
		if(pathList == null) return null;
		Vector2f[] vPath = new Vector2f[pathList.size()];
		for(int i = 0; i < pathList.size(); i++) {
			Terrain t = pathList.get(i);
			
			vPath[i] = new Vector2f(t.getX()+Terrain.IMG_WIDTH/2, t.getY()+Terrain.IMG_HEIGHT/2);
			//GameUtil.changeBright(t, map, 1.4f);
		}
		
		return vPath;
	}
}
