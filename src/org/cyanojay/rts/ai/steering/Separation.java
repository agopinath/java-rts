package org.cyanojay.rts.ai.steering;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.units.Soldier;
import org.cyanojay.rts.world.units.Swarm;

import com.agopinath.lthelogutil.Fl;

public class Separation extends SteeringBehavior {
	public static final float DESIRED_SEP = 16.0f;
	private Swarm swarm;
	public Separation(Swarm swarm) {
		this.swarm = swarm;
	}
	
	@Override
	public Vector2f getSteerForce(Vector2f position, Vector2f velocity, Object... args) {
		
		Vector2f steer = Vector2f.ZERO;
		int count = 0;

		for (Soldier other : swarm) {
			float d = Vmath.distBetween(position, other.getPosition());
			if ((d > 0) && (d < DESIRED_SEP)) {
				// Calculate vector pointing away from neighbor
				Vector2f diff = Vmath.sub(position, other.getPosition());
				diff.normalize();
				diff = Vmath.mult(diff, 100 / d); // Weight by distance
				steer = Vmath.add(steer, diff);
				count++;
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer = Vmath.mult(steer, 1 / (float) count);
		}
		
		if (steer.len() > 0) {
			Vmath.setLength(steer, Soldier.MAX_STEER); 
		}

	    return steer;
	  }
	
	@Override
	public SteeringType getSteerType() {
		return SteeringType.SEPARATION;
	}
}
