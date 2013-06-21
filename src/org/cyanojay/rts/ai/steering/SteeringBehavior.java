package org.cyanojay.rts.ai.steering;

import org.cyanojay.rts.util.vector.Vector2f;


public abstract class SteeringBehavior {
	public abstract Vector2f getSteerForce(Vector2f position, Vector2f velocity, Object... args);
	public abstract SteeringType getSteerType();
}
