package org.cyanojay.rts.ai.steering;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;

public class Seek implements SteeringBehavior {
	private final float steerSpeed;
	private Vector2f target;
	
	public Seek(Vector2f target, float steerSpeed) {
		this.setTarget(target);
		this.steerSpeed = steerSpeed;
	}
	
	@Override
	public Vector2f getSteerForce(Vector2f position, Vector2f velocity, Object... args) {
		Vector2f desiredVelocity = Vmath.sub(target, position);
		//if(Vmath.len(velocity) < maxSteer)
		//	return Vmath.mult(Vmath.normalize(Vmath.sub(desiredVelocity, velocity)), maxSteer);
		
		return Vmath.setLength(Vmath.sub(desiredVelocity, velocity), steerSpeed);
	}

	public Vector2f getTarget() {
		return target;
	}

	public void setTarget(Vector2f target) {
		this.target = target;
	}
}
