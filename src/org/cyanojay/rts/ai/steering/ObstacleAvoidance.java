package org.cyanojay.rts.ai.steering;

import java.util.List;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.map.Terrain;

public class ObstacleAvoidance extends SteeringBehavior {
	private final static int CHECK_LENGTH = 64; // distance to look ahead
	private List<Terrain> toAvoid;
	private float maxSpeed;
	
	public ObstacleAvoidance(List<Terrain> toAvoid, float maxSpeed) {
		this.toAvoid = toAvoid;
		this.maxSpeed = maxSpeed;
	}
	
	@Override
	public Vector2f getSteerForce(Vector2f position, Vector2f velocity, Object... args) {
		Vector2f force = Vector2f.ZERO;
		for(Terrain t : toAvoid) {
			Vector2f forward = Vmath.normalize(velocity);
			Vector2f diff = Vmath.sub(new Vector2f(t.getX()+Terrain.IMG_WIDTH/2, t.getY()+Terrain.IMG_HEIGHT/2), position);
			float dotP = Vmath.dot(diff, forward);
			
			if(dotP > 0) { // if object is in front of this vehicle
				Vector2f ray = Vmath.mult(forward, CHECK_LENGTH);
				Vector2f projection = Vmath.mult(forward, dotP);
				float dist = Vmath.sub(projection, diff).len();
				
				if(dist < (Terrain.AVOID_RADIUS) && projection.len() < ray.len()) {
					force = Vmath.mult(forward, maxSpeed);
					force.setAngle((float) (force.getAngle() + Vmath.sign(diff) * Math.PI/2));
					force = Vmath.mult(force, (1 - projection.len())/ray.len());
				}
				
			}
			
		}
		
		return force;
	}

	@Override
	public SteeringType getSteerType() {
		return SteeringType.OBSTACLE_AVOIDANCE;
	}
}
