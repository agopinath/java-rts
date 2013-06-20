package org.cyanojay.rts.ai.steering;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;


public class SteeringManager {
	public final static float PATH_RADIUS = 4f;
	
	private Pathway path;
	
	public SteeringManager(Pathway path) {
		this.path = path;
	}
	
	public SteeringManager() {
		this(null);
	}

	// predictionTime specifies extent of future prediction
	// direction (should be either +1 or -1) specifies direction along path (forward or backward, respectively)
	public Vector2f steerAlongPath(Vector2f currPos, Vector2f currVelocity, float predictionTime, float maxSteer, int direction) {
		return steerAlongPath(currPos, currVelocity, predictionTime, maxSteer, direction, this.path);
	}
	
	public Vector2f steerAlongPath(Vector2f currPos, Vector2f currVelocity, float predictionTime, float maxSteer, int direction, Pathway p) {
		float pathDistOffset = Vmath.len(currVelocity) * predictionTime * direction;
		Vector2f futurePos = getFuturePosition(currPos, currVelocity, predictionTime);

		float nowPathDist = p.mapPointToPathDistance(currPos);
		float futurePathDist = p.mapPointToPathDistance(futurePos);

		boolean rightway = ((pathDistOffset > 0) ? 
							(nowPathDist < futurePathDist) :
							(nowPathDist > futurePathDist));

		Vector2f onPath = p.mapPointToPath(futurePos);
		float outside = (float) (Vmath.distBetween(onPath, futurePos) - PATH_RADIUS);

		if (outside < 0 && rightway) {
			return Vector2f.ZERO;
		} else {
			float targetPathDist = nowPathDist + pathDistOffset;
			Vector2f pathTarg = p.mapDistanceToPoint(targetPathDist);
			return seek(currPos, currVelocity, pathTarg, 6f);
		}
	}
	
	public Vector2f getFuturePosition(Vector2f currPos, Vector2f currVelocity, float predictionTime) {
		return Vmath.add(currPos, Vmath.mult(currVelocity, predictionTime));
	}
	
	public Vector2f seek(Vector2f currPos, Vector2f currVelocity, Vector2f targetPos, float steerCap) {
		Vector2f desiredVelocity = Vmath.sub(targetPos, currPos);
		if(Vmath.len(currVelocity) < steerCap)
			return Vmath.mult(Vmath.normalize(Vmath.sub(desiredVelocity, currVelocity)), steerCap);
		
		return Vmath.truncate(Vmath.sub(desiredVelocity, currVelocity), steerCap);
	}
	
	public Vector2f applySteeringToVelocity(Vector2f currVelocity, Vector2f steering, float moveSpeed) {
		return Vmath.setLength(Vmath.add(currVelocity, steering), moveSpeed);
	}
	
	public void setPath(Pathway path) {
		this.path = path;
	}
}
