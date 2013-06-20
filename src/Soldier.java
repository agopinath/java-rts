import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;


public class Soldier {
	private Vector2f position;
	private Vector2f velocity;
	private final static float MOVE_SPEED = 5f;
	private final static float MAX_STEER = 2f;
	private Color color;
	private Ellipse2D.Float body;
	private Pathway path;
	private SteeringManager steer;
	
	public Soldier(Vector2f pos, Color c) {
		position = pos;
		color = c;
		body = new Ellipse2D.Float(position.x, position.y, 12, 12);
		
		velocity = new Vector2f();
		
		steer = new SteeringManager(null);
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
		Vector2f steering = steer.steerAlongPath(position, velocity, 20f, MAX_STEER, 1);
		applySteeringForces(steering);
	}
	
	private void applySteeringForces(Vector2f steering) {
		velocity = Vmath.setLength(Vmath.add(velocity, steering), MOVE_SPEED);
		position = steer.getFuturePosition(position, velocity, 1f);
		
		setPosition(position);
		
		if(Vmath.distBetween(position, path.getPathVectorAt(path.getPathSize()-1)) < 16) {
			velocity = Vector2f.ZERO;
			path = null;
		}
	}

	public void setPath(Vector2f[] p) {
		Collections.reverse(Arrays.asList(p));
		this.path = new Pathway(p);
		steer.setPath(path);
	}
}
