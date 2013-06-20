package org.cyanojay.rts.world.units;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cyanojay.rts.ai.steering.Pathway;
import org.cyanojay.rts.ai.steering.SteeringManager;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;

import com.agopinath.lthelogutil.Fl;

public class Swarm implements Iterable<Soldier> {
	private Set<Soldier> units;
	private Pathway path;
	private SteeringManager steer;
	
	public Swarm() {
		units = new HashSet<Soldier>();
		steer = new SteeringManager();
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
	
	public void setPath(Vector2f[] p) {
		Collections.reverse(Arrays.asList(p));
		this.path = new Pathway(p);
		steer.setPath(path);
	}
	
	public void update() {
		if(path == null) return;
		
		int numArrived = 0;;
		for(Soldier s : units) {
			if(updateUnit(s)) numArrived++;
		}
		
		if(numArrived == units.size()) {
			Fl.og("Swarm arrived");
			path = null;
		}
	}
	
	private boolean updateUnit(Soldier s) {
		Vector2f steerForce = steer.steerAlongPath(s.getPosition(), s.getVelocity(), 20f, Soldier.MAX_STEER, 1);
		s.setVelocity(Vmath.setLength(Vmath.add(s.getVelocity(), steerForce), Soldier.MOVE_SPEED));
		s.setPosition(Vmath.add(s.getPosition(), s.getVelocity()));
		
		if(Vmath.distBetween(s.getPosition(), path.getPathVectorAt(path.getPathSize()-1)) < 16) {
			s.setVelocity(Vector2f.ZERO);
			return true;
		}
		
		return false;
	}
}
