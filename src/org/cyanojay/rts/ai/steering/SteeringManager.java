package org.cyanojay.rts.ai.steering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.units.Soldier;
import org.cyanojay.rts.world.units.Swarm;


public class SteeringManager {
	private Map<Soldier, List<SteeringBehavior>> behaviors;
	private Map<Soldier, List<Float>> weightMap;
	
	public SteeringManager() {
		behaviors = new HashMap<Soldier, List<SteeringBehavior>>();
		weightMap = new HashMap<Soldier, List<Float>>();
	}
	
	public void addUnit(Soldier s) {
		behaviors.put(s, new ArrayList<SteeringBehavior>());
		weightMap.put(s, new ArrayList<Float>());
	}
	
	public void addBehavior(SteeringBehavior behavior, float weight) {
		for(Soldier s : behaviors.keySet()) {
			List<SteeringBehavior> unitBehaviors = behaviors.get(s);
			List<Float> weights = weightMap.get(s);
			unitBehaviors.add(behavior);
			weights.add(weight);
		}
	}
	
	public void removeBehavior(Soldier s, int i) {
		behaviors.get(s).remove(i);
		weightMap.get(s).remove(i);
	}
	
	public void setWeight(Soldier s, int i, float newWeight) {
		weightMap.get(s).set(i, newWeight);
	}
	
	// predictionTime specifies extent of future prediction
	// direction (should be either +1 or -1) specifies direction along path (forward or backward, respectively)
	/*public Vector2f steerAlongPath(Vector2f currPos, Vector2f currVelocity, float predictionTime, float maxSteer, int direction) {
		return steerAlongPath(currPos, currVelocity, predictionTime, maxSteer, direction, this.path);
	}*/
	
	public Vector2f steer(Soldier s, Vector2f pos, Vector2f velocity, Object... args) {
		Vector2f weightedSteer = Vector2f.ZERO;
		List<SteeringBehavior> unitBehaviors = behaviors.get(s);
		List<Float> weights = weightMap.get(s);
		for(int i = 0 ; i < unitBehaviors.size(); i++) {
			Vector2f currSteer = Vmath.mult(unitBehaviors.get(i).getSteerForce(pos, velocity, args), weights.get(i));
			weightedSteer = Vmath.add(weightedSteer, currSteer);
		}
		
		return weightedSteer;//behaviors.get(0).getSteerForce(pos, velocity, args);
	}
	
	public static Vector2f applySteeringToVelocity(Vector2f currVelocity, Vector2f steering, float moveSpeed) {
		return Vmath.setLength(Vmath.add(currVelocity, steering), moveSpeed);
	}
}
