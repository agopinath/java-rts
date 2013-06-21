package org.cyanojay.rts.ai.steering;

import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;

import com.agopinath.lthelogutil.Fl;

public class FollowPath implements SteeringBehavior {
	public final static float PATH_RADIUS = 4f;
	private final float predictionTime;
	private final int direction;
	private Pathway path;
	private Seek seek;
	
	public FollowPath(float predictionTime, float maxSteer, int direction, Pathway path) {
		this.predictionTime = predictionTime;
		this.direction = direction;
		this.path = path;
		seek = new Seek(null, maxSteer);
	}
	
	@Override
	public Vector2f getSteerForce(Vector2f pos, Vector2f vel, Object... args) {
		path = (Pathway) args[0];
		float pathDistOffset = vel.len() * predictionTime * direction;
		Vector2f futurePos = getFuturePosition(pos, vel, predictionTime);

		float nowPathDist = path.mapPointToPathDistance(pos);
		float futurePathDist = path.mapPointToPathDistance(futurePos);

		boolean rightway = ((pathDistOffset > 0) ? 
							(nowPathDist < futurePathDist) :
							(nowPathDist > futurePathDist));

		Vector2f onPath = path.mapPointToPath(futurePos);
		float outside = (float) (Vmath.distBetween(onPath, futurePos) - PATH_RADIUS);

		if (outside < 0 && rightway) {
			return Vector2f.ZERO;
		} else {
			float targetPathDist = nowPathDist + pathDistOffset;
			Vector2f pathTarg = path.mapDistanceToPoint(targetPathDist);
			seek.setTarget(pathTarg);
			return seek.getSteerForce(pos, vel);
		}
		
	}
	
	private Vector2f getFuturePosition(Vector2f currPos, Vector2f currVelocity, float predictionTime) {
		return Vmath.add(currPos, Vmath.mult(currVelocity, predictionTime));
	}
}
