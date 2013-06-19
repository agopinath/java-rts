import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;


public class Soldier {
	private Vector2f position;
	private Vector2f velocity;
	private final static float MAX_SPEED = 5f;
	private final static float MAX_STEER = 1f;
	private Color color;
	private Ellipse2D.Float body;
	private PolylinePathway path;
	Vector2f futurePos;
	Vector2f onPath;
	Vector2f pathTarg;
	private int pathRad;
	
	public Soldier(Vector2f pos, Color c) {
		position = pos;
		color = c;
		body = new Ellipse2D.Float(position.x, position.y, 12, 12);
		
		velocity = new Vector2f();
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fill(body);
	}
	
	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
		body.x = position.x;
		body.y = position.y;
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}
	
	public void update() {
		if(path == null) return;
		Vector2f steering = followPath(20f, 1, pathRad);
		applySteeringForces(steering);
	}
	
	// predictionTime specifies extent of future prediction
	// direction (should be either +1 or -1) specifies direction along path (forward or backward, respectively)
	private Vector2f followPath(float predictionTime, int direction, float pathrad) { 
		float pathDistOffset = Vmath.len(velocity) * predictionTime * direction;		
		futurePos = getFuturePosition(predictionTime);
		
		float nowPathDist = path.mapPointToPathDistance(position);
		float futurePathDist = path.mapPointToPathDistance(futurePos);
		
		boolean rightway = ((pathDistOffset > 0) ?
			                (nowPathDist < futurePathDist) :
			                (nowPathDist > futurePathDist));
		
		onPath = path.mapPointToPath(futurePos);
		float outside = (float) (PolylinePathway.distBetween(onPath, futurePos) - pathrad);
		
		if(outside < 0 && rightway) {
			return Vector2f.ZERO;
		} else {
			float targetPathDist = nowPathDist + pathDistOffset;
			pathTarg = path.mapDistanceToPoint(targetPathDist);
			return seek(pathTarg);
		}
	}
	
	private Vector2f getFuturePosition(float predictionTime) {
		return Vmath.add(position, Vmath.mult(velocity, predictionTime));
	}

	private Vector2f seek(Vector2f targetPos) {
		Vector2f desiredVelocity = Vmath.sub(targetPos, position);
		if(Vmath.len(velocity) < MAX_STEER)
			return Vmath.mult(Vmath.normalize(Vmath.sub(desiredVelocity, velocity)), MAX_STEER);
		
		return Vmath.truncate(Vmath.sub(desiredVelocity, velocity), MAX_STEER);
	}
	
	private void applySteeringForces(Vector2f steering) {
		velocity = Vmath.setLength(Vmath.add(velocity, steering), MAX_SPEED);
		position = getFuturePosition(1f);
		
		setPosition(position);
		
		if(PolylinePathway.distBetween(position, path.getPathPositionAt(path.getPathLength()-1)) < 16) {
			velocity = Vector2f.ZERO;
			path = null;
		}
	}
	
	/*public PolylinePathway getPath() {
		return path;
	}*/

	public void setPath(Vector2f[] p, int pathrad) {
		//velocity = p[0].sub(position);
		//velocity = velocity.truncate(MAX_SPEED);
		
		this.pathRad = pathrad;
		
		Collections.reverse(Arrays.asList(p));
		this.path = new PolylinePathway(p);
	}
}
