package org.cyanojay.rts.ai.steering;

import java.util.ArrayList;
import java.util.List;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;


public class SteeringManager {
	private List<SteeringBehavior> behaviors;
	private List<Float> weights;
	
	public SteeringManager() {
		behaviors = new ArrayList<SteeringBehavior>();
		weights = new ArrayList<Float>();
	}
	
	public void addBehavior(SteeringBehavior behavior, float weight) {
		behaviors.add(behavior);
		weights.add(weight);
	}
	
	// predictionTime specifies extent of future prediction
	// direction (should be either +1 or -1) specifies direction along path (forward or backward, respectively)
	/*public Vector2f steerAlongPath(Vector2f currPos, Vector2f currVelocity, float predictionTime, float maxSteer, int direction) {
		return steerAlongPath(currPos, currVelocity, predictionTime, maxSteer, direction, this.path);
	}*/
	
	public Vector2f steer(Vector2f pos, Vector2f velocity, Object... args) {
		Vector2f weightedSteer = Vector2f.ZERO;
		for(int i = 0 ; i < behaviors.size(); i++) {
			float weight = weights.get(i);
			Vector2f currSteer = Vmath.mult(behaviors.get(i).getSteerForce(pos, velocity, args), weight);
			weightedSteer = Vmath.add(weightedSteer, currSteer);
		}
		
		return weightedSteer;//behaviors.get(0).getSteerForce(pos, velocity, args);
	}
	
	/*public Vector2f seek(Vector2f currPos, Vector2f currVelocity, Vector2f targetPos, float steerCap) {

	}*/
	
	public static Vector2f applySteeringToVelocity(Vector2f currVelocity, Vector2f steering, float moveSpeed) {
		return Vmath.setLength(Vmath.add(currVelocity, steering), moveSpeed);
	}
}
